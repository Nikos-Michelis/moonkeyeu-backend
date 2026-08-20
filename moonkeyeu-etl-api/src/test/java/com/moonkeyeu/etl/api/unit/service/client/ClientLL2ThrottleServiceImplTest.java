package com.moonkeyeu.etl.api.unit.service.client;

import com.moonkeyeu.etl.api.dto.LL2Throttle;
import com.moonkeyeu.etl.api.service.impl.client.ClientThrottleServiceImpl;
import com.moonkeyeu.etl.api.settings.exceptions.RemoteServiceException;
import com.moonkeyeu.etl.api.utils.UrlBuilderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ClientLL2ThrottleServiceImplTest {
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.Builder webClientBuilder;
    private ClientThrottleServiceImpl throttleService;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private UrlBuilderUtil urlBuilderUtil;
    private LL2Throttle responseZeroUseSeconds;
    private LL2Throttle responseUseSeconds;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build())
                .thenReturn(webClient);
        when(webClient.get())
                .thenReturn(requestHeadersUriSpec);
        when(urlBuilderUtil.getThrottleUrl())
                .thenReturn(URI.create("https://www.example.com/throttle"));
        when(requestHeadersUriSpec.uri(any(URI.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);

        throttleService =
                new ClientThrottleServiceImpl(webClientBuilder.build(), urlBuilderUtil);

        responseZeroUseSeconds = new LL2Throttle(
                15,
                10,
                -100,
                3600L,
                "123.123.2.123"
        );

        responseUseSeconds = new LL2Throttle(
                15,
                15,
                100,
                3600L,
                "123.123.2.123"
        );
    }

    @Test
    void fetchThrottle_shouldFetchThrottle_whenCurrentUseReachLimit() {
        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(LL2Throttle.class))
                .thenReturn(Mono.just(responseUseSeconds));

        Mono<LL2Throttle> result = throttleService.fetchThrottle();

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(100L, response.nextUseSeconds()))
                .verifyComplete();

        verify(webClient, times(1)).get();
    }

    @Test
    void shouldReturnPositiveRemainingLimit_whenCurrentUseIsBelowRequestLimit() {
        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(LL2Throttle.class))
                .thenReturn(Mono.just(responseZeroUseSeconds));

        Mono<LL2Throttle> result = throttleService.fetchThrottle();

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(100L, response.nextUseSeconds()))
                .verifyComplete();

        verify(webClient, times(1)).get();
    }

    @Test
    void fetchThrottle_shouldReturnRemoteServiceException_for5xxError() {
        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(LL2Throttle.class))
                .thenReturn(Mono.error(
                        new RemoteServiceException(
                                "Remote service unavailable.",
                                HttpStatus.SERVICE_UNAVAILABLE
                        )
                ));

        StepVerifier.create(throttleService.fetchThrottle())
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(RemoteServiceException.class);
                    RemoteServiceException ex = (RemoteServiceException) error;
                    assertEquals("Remote service unavailable.", ex.getMessage());
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatus());
                })
                .verify();
    }

    @Test
    void fetchThrottle_shouldReturnRemoteServiceException_for4xxError() {
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);

        when(responseSpec.bodyToMono(LL2Throttle.class))
                .thenReturn(Mono.error(
                        new RemoteServiceException(
                                "Bad request to remote service.",
                                HttpStatus.BAD_REQUEST
                        )
                ));

        StepVerifier.create(throttleService.fetchThrottle())
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(RemoteServiceException.class);
                    RemoteServiceException ex = (RemoteServiceException) error;
                    assertEquals("Bad request to remote service.", ex.getMessage());
                    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
                })
                .verify();
    }
}