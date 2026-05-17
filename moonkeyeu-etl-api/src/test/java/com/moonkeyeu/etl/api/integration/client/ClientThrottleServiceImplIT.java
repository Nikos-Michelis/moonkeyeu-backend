package com.moonkeyeu.etl.api.integration.client;

import com.moonkeyeu.etl.api.service.impl.client.ClientThrottleServiceImpl;
import com.moonkeyeu.etl.api.settings.exceptions.RemoteServiceException;
import com.moonkeyeu.etl.api.utils.UrlBuilderUtil;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientThrottleServiceImplIT Integration Tests")
class ClientThrottleServiceImplIT {

    private MockWebServer mockWebServer;
    private ClientThrottleServiceImpl service;
    private UrlBuilderUtil urlBuilderUtil;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        urlBuilderUtil = mock(UrlBuilderUtil.class);

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        service = new ClientThrottleServiceImpl(webClient, urlBuilderUtil);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("Should return throttle response when remote service returns 200")
    void shouldReturnThrottleResponse_whenServiceReturns200() {

        String body = """
            {
                "your_request_limit": 15,
                "current_use": 10,
                "next_use_secs": 20,
                "limit_frequency_secs": 3600
            }
            """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        when(urlBuilderUtil.getThrottleUrl())
                .thenReturn(URI.create(mockWebServer.url("/throttle").toString()));

        StepVerifier.create(service.fetchThrottle())
                .assertNext(response -> {
                    assertEquals(15, response.requestLimit());
                    assertEquals(10, response.currentUse());
                    assertEquals(20, response.nextUseSeconds());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw RemoteServiceException when server returns 5xx")
    void shouldThrowException_whenServerReturns5xx() {

        for (int i = 500; i <= 599; i++) {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(i));
        }

        when(urlBuilderUtil.getThrottleUrl())
                .thenReturn(URI.create(mockWebServer.url("/throttle").toString()));

        StepVerifier.create(service.fetchThrottle())
                .expectErrorMatches(error ->
                        error instanceof RemoteServiceException &&
                                ((RemoteServiceException) error).getStatus().value() == 503
                )
                .verify();
    }

    @Test
    @DisplayName("Should throw RemoteServiceException when server returns 4xx")
    void shouldThrowException_whenServerReturns4xx() {

        for (int i = 400; i <= 499; i++) {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(i));
        }

        when(urlBuilderUtil.getThrottleUrl())
                .thenReturn(URI.create(mockWebServer.url("/throttle").toString()));

        StepVerifier.create(service.fetchThrottle())
                .expectError(RemoteServiceException.class)
                .verify();
    }
}