package com.moonkeyeu.etl.api.service.impl.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.moonkeyeu.etl.api.service.ClientDataService;
import com.moonkeyeu.etl.api.service.ClientThrottleService;
import com.moonkeyeu.etl.api.settings.exceptions.RateLimitExceededException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class ClientDataServiceImpl implements ClientDataService {

    private final WebClient webClient;
    private final ClientThrottleService throttleService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ClientDataServiceImpl(WebClient webClient, ClientThrottleService clientThrottleService, @Qualifier("objectMapper") ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.throttleService = clientThrottleService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> fetchData(URI url, String fileName) {
        List<JsonNode> allResults = new ArrayList<>();
        return throttleService.getThrottleDelay()
                .flatMap(delay -> {
                    if (delay > 0) {
                        log.warn("Throttle delay detected: Waiting for {} seconds.", delay);
                        return Mono.delay(Duration.ofSeconds(delay));
                    }
                    return Mono.empty();
                })
                .then(fetchNextPage(url, allResults))
                .expand(response -> hasNextPage(response) ? fetchThrottle(getNextPageUrl(response), allResults) : Mono.empty())
                .then()
                .doOnSubscribe(subscription -> {
                    log.info("Fetching process started.");
                })
                .doOnSuccess(done -> {
                    saveJsonData(allResults, fileName);
                    log.info("Fetching process successfully completed.");
                })
                .doOnError(error -> {
                    log.error("An error occurred during fetching: {}", error.getMessage());
                });
    }

    @Override
    public Mono<JsonNode> fetchThrottle(URI url, List<JsonNode> allResults) {
        return throttleService.getThrottleDelay()
                .flatMap(delay -> {
                    if (delay > 0) {
                        log.warn("Throttling applied. Waiting for {} seconds.", delay);
                        return Mono.delay(Duration.ofSeconds(delay)).then(fetchNextPage(url, allResults));
                    } else {
                        return fetchNextPage(url, allResults);
                    }
                });
    }

    @Override
    public Mono<JsonNode> fetchNextPage(URI url, List<JsonNode> allResults) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.value() == 429, clientResponse -> {
                    log.warn("Rate limit hit (429). Fetching delay...");
                    return throttleService.getThrottleDelay()
                            .flatMap(delay -> {
                                if (delay > 0) {
                                    return Mono.delay(Duration.ofSeconds(delay))
                                            .then(Mono.error(new RateLimitExceededException("Rate limit hit. Waiting for " + delay + " seconds...", delay)));
                                }
                                return Mono.empty();
                            });
                })
                .bodyToMono(JsonNode.class)
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(2))
                        .maxBackoff(Duration.ofMinutes(3))
                        .filter(this::hasThrownAnyOfException))
                .doOnSubscribe(subscription -> {
                    log.info("Request started, awaiting long response...");
                })
                .doOnError(error -> log.error("Error fetching data: {}", error.getMessage()))
                .doOnNext(response -> {
                    if (response != null) {
                        allResults.add(response);
                    }
                });
    }

    public void saveJsonData(List<JsonNode> allResults, String fileName) {
        try {
            ObjectNode finalResult = objectMapper.createObjectNode();
            finalResult.set("all_results", objectMapper.valueToTree(allResults));
            Files.write(Paths.get(fileName), objectMapper.writeValueAsBytes(finalResult),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            log.info("Data written to JSON file: {}", fileName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Somthing went wrong during Json tree parsing." + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("An unexpected error occurred." + e.getMessage());
        }
    }

    private boolean hasNextPage(JsonNode response) {
        JsonNode nextPage = response.get("next");
        log.info("next --> {}", nextPage);
        return nextPage != null && !nextPage.isNull();
    }

    private boolean hasThrownAnyOfException(Throwable throwable) {
        return throwable instanceof RateLimitExceededException ||
                        throwable instanceof WebClientResponseException ||
                        throwable instanceof TimeoutException ||
                        throwable instanceof IOException;
    }

    private URI getNextPageUrl(JsonNode response) {
        return URI.create(response.get("next").asText());
    }

}
