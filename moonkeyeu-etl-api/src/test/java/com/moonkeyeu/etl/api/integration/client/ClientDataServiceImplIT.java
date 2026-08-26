package com.moonkeyeu.etl.api.integration.client;

import com.moonkeyeu.etl.api.config.TestEntity;
import com.moonkeyeu.etl.api.dto.LL2Throttle;
import com.moonkeyeu.etl.api.service.ClientThrottleService;
import com.moonkeyeu.etl.api.service.impl.client.ClientDataServiceImpl;
import com.moonkeyeu.etl.api.settings.exceptions.RateLimitExceededException;
import com.moonkeyeu.etl.api.utils.JsonStreamFileWriterUtil;
import com.moonkeyeu.etl.api.utils.LL2URIBuilder;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.handler.timeout.WriteTimeoutHandler;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.test.StepVerifier;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientDataServiceImpl Integration Tests")
class ClientDataServiceImplIT {
    @Mock
    private ClientThrottleService clientThrottleService;
    private MockWebServer mockWebServer;
    private ClientDataServiceImpl clientDataService;
    private ObjectMapper objectMapper;
    private LL2URIBuilder LL2URIBuilder;
    private JsonGenerator jsonGenerator;
    private LL2Throttle responseZeroUseSeconds;
    private LL2Throttle responseUseSeconds;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        LL2URIBuilder = new LL2URIBuilder();
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() throws IOException {
        String baseUrl = String.format("http://localhost:%d", mockWebServer.getPort());
        Path outputFile = tempDir.resolve("output.json");
        jsonGenerator = objectMapper.createGenerator(Files.newOutputStream(outputFile), JsonEncoding.UTF8);
        responseZeroUseSeconds = new LL2Throttle(15, 10, 1, 3600L, "123.123.2.123");
        responseUseSeconds = new LL2Throttle(15, 10, 1, 3600L, "123.123.0.123");

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(2))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                .doOnConnected(c -> c
                        .addHandlerLast(new ReadTimeoutHandler(20))
                        .addHandlerLast(new WriteTimeoutHandler(20)));

        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(mockWebServer.url(baseUrl).toString())
                .defaultHeader(HttpHeaders.USER_AGENT, "Test Client")
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(20 * 1024 * 1024))
                .build();

        JsonStreamFileWriterUtil jsonStreamFileWriterUtil = new JsonStreamFileWriterUtil(objectMapper);
        clientDataService = new ClientDataServiceImpl(
                webClient,
                clientThrottleService,
                jsonStreamFileWriterUtil
        );

        ReflectionTestUtils.setField(LL2URIBuilder, "baseUrl", baseUrl);
        ReflectionTestUtils.setField(LL2URIBuilder, "version", "2.3.0");
        ReflectionTestUtils.setField(clientDataService, "MAX_RETRIES", 30);
        ReflectionTestUtils.setField(clientDataService, "RETRY_DELAY", 1);
    }

    @Test
    @DisplayName("Should stop pagination when next is null")
    void fetchAll_shouldStopPagination_whenNextIsNull() {
        // given
        String page1 = createResponse(null, TestEntity.builder().id("1").value("Launch 1").build());

        mockWebServer.enqueue(new MockResponse()
                .setBody(page1)
                .addHeader("Content-Type", "application/json"));

        String outputFile = tempDir.resolve("output.json").toString();

        when(clientThrottleService.fetchThrottle())
                .thenReturn(Mono.just(responseZeroUseSeconds));
        // when
        StepVerifier.create(clientDataService.fetchAll(LL2URIBuilder.launchesFromURI(LocalDate.now()), outputFile))
                .expectComplete()
                .verify();

        // then
        assertEquals(1, mockWebServer.getRequestCount());
    }

    @Test
    @DisplayName("Should fetch all pages and write to file")
    void fetchAll_shouldFetchMultiplePages_whenNextIsNotNull() {
        // given
        String page1 = createResponse("/page2", TestEntity.builder().id("1").value("Launch 1").build());
        String page2 = createResponse(null, TestEntity.builder().id("2").value("Launch 2").build());

        mockWebServer.enqueue(new MockResponse()
                .setBody(page1)
                .addHeader("Content-Type", "application/json"));

        mockWebServer.enqueue(new MockResponse()
                .setBody(page2)
                .addHeader("Content-Type", "application/json"));

        String outputFile = tempDir.resolve("output.json").toString();


        when(clientThrottleService.fetchThrottle())
                .thenReturn(Mono.just(responseZeroUseSeconds));
        // when
        StepVerifier.create(clientDataService.fetchAll(LL2URIBuilder.launchesFromURI(LocalDate.now()), outputFile))
                .expectComplete()
                .verify();

        // then
        assertEquals(2, mockWebServer.getRequestCount());
        JsonNode fileContent = objectMapper.readTree(new File(outputFile));
        assertEquals(1, fileContent.size());
    }

    @Test
    @DisplayName("Should retry when reponed with status code 429 and applyStrategy throttle")
    void fetchAll_shouldRetry_whenStatusCode429() {
        // given
        String page1 = createResponse("/page2", TestEntity.builder().id("1").value("Launch 1").build());
        String page2 = createResponse("/page3", TestEntity.builder().id("2").value("Launch 2").build());
        String page3 = createResponse(null, TestEntity.builder().id("3").value("Launch 3").build());

        mockWebServer = enqueueMockResponses(30, new MockResponse().setResponseCode(429));

        mockWebServer.enqueue(new MockResponse()
                .setBody(page1)
                .addHeader("Content-Type", "application/json"));

        mockWebServer.enqueue(new MockResponse()
                .setBody(page2)
                .addHeader("Content-Type", "application/json"));

        mockWebServer = enqueueMockResponses(10, new MockResponse().setResponseCode(429));

        mockWebServer.enqueue(new MockResponse()
                .setBody(page3)
                .addHeader("Content-Type", "application/json"));

        String outputFile = tempDir.resolve("output.json").toString();


        when(clientThrottleService.fetchThrottle())
                .thenReturn(Mono.just(responseUseSeconds));
        // when
        StepVerifier.create(clientDataService.fetchAll(mockWebServer.url("/page1").uri(), outputFile))
                .expectComplete()
                .verify();

        // then
        assertEquals(43, mockWebServer.getRequestCount());
        JsonNode fileContent = objectMapper.readTree(new File(outputFile));
        assertEquals(1, fileContent.size());
    }

    @Test
    @DisplayName("Should complete immediately when next page URL is null")
    void fetchNext_shouldReturnEmpty_whenUrlIsNull() {
        StepVerifier.create(clientDataService.fetchNext(null, "test.json", jsonGenerator))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should stop retry when maximum retries exceeded")
    void fetch_shouldStopRetry_whenMaximumRetriesExceeded() {
        // given
        mockWebServer = enqueueMockResponses(31, new MockResponse().setResponseCode(429));

        when(clientThrottleService.fetchThrottle())
                .thenReturn(Mono.just(responseUseSeconds));
        // when
        StepVerifier.create(clientDataService.fetch(mockWebServer.url("/page1").uri()))
                .expectError(RateLimitExceededException.class)
                .verify();

        // then
        assertEquals(31, mockWebServer.getRequestCount());
    }

    @Test
    @DisplayName("Should throw timeout exception and retry")
    void fetch_shouldThrowTimeoutException_onSlowResponse() {
        // given
        mockWebServer = enqueueMockResponses(3,
                new MockResponse()
                        .setSocketPolicy(SocketPolicy.NO_RESPONSE)
                        .setBodyDelay(121, TimeUnit.SECONDS)
        );

        Mono<JsonNode> result = clientDataService.fetch(mockWebServer.url("/page1").uri()).timeout(Duration.ofSeconds(121));
        // when
        StepVerifier.create(result)
                .expectErrorMatches(Exceptions::isRetryExhausted)
                .verify();

        // then
        assertEquals(6, mockWebServer.getRequestCount());
    }

    @Test
    void fetch_shouldRetriesOnConnectionReset() {
        mockWebServer.enqueue(new MockResponse()
                .setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"count\":1}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(clientDataService.fetch(mockWebServer.url("/page1").uri()))
                .expectNextCount(1)
                .verifyComplete();

        assertEquals(2, mockWebServer.getRequestCount());
    }

    private MockWebServer enqueueMockResponses(int totalRequests, MockResponse mockResponse) {
        for (int i = 0; i < totalRequests; i++) {
            mockWebServer.enqueue(mockResponse);
        }

        return mockWebServer;
    }

    private String createResponse(String next, TestEntity testEntity) {
        String nextValue = next != null
                ? "\"" + mockWebServer.url(next) + "\""
                : "null";

        return String.format("""
        {
            "results": [{"id": "%s", "name": "%s"}],
            "next": %s
        }
        """, testEntity.getId(), testEntity.getValue(), nextValue);
    }
}