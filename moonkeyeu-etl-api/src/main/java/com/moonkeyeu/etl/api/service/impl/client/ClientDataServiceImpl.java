package com.moonkeyeu.etl.api.service.impl.client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.service.ClientDataService;
import com.moonkeyeu.etl.api.service.ClientThrottleService;
import com.moonkeyeu.etl.api.settings.exceptions.RateLimitExceededException;
import com.moonkeyeu.etl.api.utils.JsonStreamFileWriterUtil;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class ClientDataServiceImpl implements ClientDataService {

    private final WebClient webClient;
    private final ClientThrottleService throttleService;
    private final JsonStreamFileWriterUtil jsonStreamFileWriterUtil;
    @Value("${application.webclient.max-retries}")
    private Integer MAX_RETRIES;
    @Value("${application.webclient.retry-delay-seconds}")
    private Integer RETRY_DELAY;
    @Autowired
    public ClientDataServiceImpl(WebClient webClient, ClientThrottleService clientThrottleService, JsonStreamFileWriterUtil jsonStreamFileWriterUtil) {
        this.webClient = webClient;
        this.throttleService = clientThrottleService;
        this.jsonStreamFileWriterUtil = jsonStreamFileWriterUtil;
    }

    @Override
    public Mono<Void> fetchAll(URI url, String fileName) {
        return jsonStreamFileWriterUtil.open(fileName)
                .flatMap(generator ->
                        fetchNext(url, fileName, generator).doOnTerminate(() -> jsonStreamFileWriterUtil.close(generator))
                )
                .doOnSuccess(done -> log.info("Fetching process successfully completed."))
                .doOnError(error -> log.error("An error occurred during fetching: {}", error.getMessage()));
    }

    @Override
    public Mono<Void> fetchNext(URI url, String fileName, JsonGenerator generator) {
        if (url == null) {
            log.info("No next page. Stopping pagination.");
            return Mono.empty();
        }

        return fetchThrottle()
                .then(fetch(url))
                .flatMap(response -> jsonStreamFileWriterUtil.write(generator, response).thenReturn(response))
                .flatMap(response -> {
                    if (!hasNextPage(response)) return Mono.empty();
                    URI nextUrl = getNextPageUrl(response);
                    return Mono.defer(() -> fetchNext(nextUrl, fileName, generator));
                });
    }

    private Mono<Long> fetchThrottle() {
        return throttleService.fetchThrottle()
                .flatMap(throttle -> {
                    long delay = throttle.nextUseSeconds();
                    if (delay > 0) {
                        log.info("Request limit reached. Waiting for {} seconds...", delay);
                        return Mono.delay(Duration.ofSeconds(delay)).thenReturn(delay);
                    }

                    return Mono.just(0L);
                });
    }

    @Override
    public Mono<JsonNode> fetch(URI url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        return Mono.error(new RateLimitExceededException("Rate limit hit."));
                    }
                    return Mono.empty();
                })
                .bodyToMono(JsonNode.class)
                .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(RETRY_DELAY))
                        .filter(this::isNetworkError))
                .retryWhen(Retry.from(companion ->
                        companion.flatMap(this::handleRateLimitRetry)))
                .doOnSubscribe(s -> log.info("Request started for: {}", url))
                .doOnError(error -> log.error("Error fetching data: {}", error.getMessage()));
    }

    private Mono<Long> handleRateLimitRetry(Retry.RetrySignal retrySignal) {
        if (retrySignal.totalRetries() >= MAX_RETRIES) {
            return Mono.error(retrySignal.failure());
        }

        if (retrySignal.failure() instanceof RateLimitExceededException) {
            return throttleService.fetchThrottle()
                    .flatMap(throttle -> {
                        long delay =  throttle.nextUseSeconds() > 0 ? throttle.nextUseSeconds() : RETRY_DELAY;
                        log.warn("Rate limit exceeded. Waiting {} seconds (retry {}/{})", delay, retrySignal.totalRetries() + 1, MAX_RETRIES);
                        return Mono.delay(Duration.ofSeconds(delay));
                    })
                    .thenReturn(retrySignal.totalRetries());
        }

        return Mono.error(retrySignal.failure());
    }

    private boolean isNetworkError(Throwable throwable) {
        return throwable instanceof WebClientResponseException ||
                throwable instanceof WriteTimeoutException ||
                throwable instanceof ReadTimeoutException ||
                throwable instanceof TimeoutException ||
                throwable instanceof IOException;
    }

    private boolean hasNextPage(JsonNode response) {
        JsonNode nextPage = response.get("next");
        log.info("next --> {}", nextPage);
        return nextPage != null && !nextPage.isNull();
    }

    private URI getNextPageUrl(JsonNode response) {
        return URI.create(response.get("next").asText());
    }
}