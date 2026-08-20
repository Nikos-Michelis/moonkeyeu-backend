package com.moonkeyeu.etl.api.service.impl.client;

import com.moonkeyeu.etl.api.dto.LL2Throttle;
import com.moonkeyeu.etl.api.service.ClientThrottleService;
import com.moonkeyeu.etl.api.settings.exceptions.RemoteServiceException;
import com.moonkeyeu.etl.api.utils.UrlBuilderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;


@Service
public class ClientThrottleServiceImpl implements ClientThrottleService {
    private final WebClient webClient;
    private final UrlBuilderUtil urlBuilderUtil;

    @Autowired
    public ClientThrottleServiceImpl(WebClient webClient, UrlBuilderUtil urlBuilderUtil) {
        this.webClient = webClient;
        this.urlBuilderUtil = urlBuilderUtil;
    }

    @Override
    public Mono<LL2Throttle> fetchThrottle() {
        URI throttleUri = urlBuilderUtil.getThrottleUrl();
        return webClient.get()
                .uri(throttleUri)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        response ->
                                Mono.error(new RemoteServiceException("Remote service unavailable.", HttpStatus.SERVICE_UNAVAILABLE)
                        )
                )
                .onStatus(HttpStatusCode::is4xxClientError,
                        response ->
                                Mono.error(new RemoteServiceException("Bad request to remote service.", HttpStatus.BAD_REQUEST)
                        )
                )
                .bodyToMono(LL2Throttle.class);
    }
}