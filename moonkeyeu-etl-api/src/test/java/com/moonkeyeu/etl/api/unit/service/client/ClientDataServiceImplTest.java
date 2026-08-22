package com.moonkeyeu.etl.api.unit.service.client;

import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.moonkeyeu.etl.api.dto.LL2Throttle;
import com.moonkeyeu.etl.api.service.ClientThrottleService;
import com.moonkeyeu.etl.api.service.impl.client.ClientDataServiceImpl;
import com.moonkeyeu.etl.api.settings.exceptions.RateLimitExceededException;
import com.moonkeyeu.etl.api.utils.JsonStreamFileWriterUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientDataServiceImpl Unit Tests")
class ClientDataServiceImplTest {

    @Mock
    private WebClient webClient;
    @Mock
    private ClientThrottleService throttleService;
    @Mock
    private JsonStreamFileWriterUtil jsonStreamFileWriterUtil;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private JsonGenerator jsonGenerator;
    @TempDir
    Path tempDir;
    private ClientDataServiceImpl clientDataService;
    private LL2Throttle responseZeroUseSeconds;
    private LL2Throttle responseUseSeconds;

    @BeforeEach
    void setUp() {
        clientDataService = new ClientDataServiceImpl(webClient, throttleService, jsonStreamFileWriterUtil);
        responseZeroUseSeconds = new LL2Throttle(
                15,
                10,
                0,
                3600L,
                "123.123.0.123"
        );
        responseUseSeconds = new LL2Throttle(
                15,
                10,
                10,
                3600L,
                "123.123.0.123"
        );
        ReflectionTestUtils.setField(clientDataService, "MAX_RETRIES", 30);
        ReflectionTestUtils.setField(clientDataService, "RETRY_DELAY", 1);
    }

    private JsonNode jsonFromFile(String pathResources) throws IOException {
        ClassPathResource resource = new ClassPathResource(pathResources);
        Path jsonFilePath = resource.getFile().toPath();

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(Files.readString(jsonFilePath));
    }

    @Test
    @DisplayName("Should fetch single page when no next page exists")
    void fetchAll_shouldFetchSinglePage_whenNextNotPageExists() throws IOException {
        JsonNode singlePage = jsonFromFile("/page_final.json");
        URI url = URI.create("https://api.example.com/2.3.0/launches");
        String fileName = tempDir.resolve("output.json").toString();

        when(throttleService.fetchThrottle()).thenReturn(Mono.just(responseZeroUseSeconds));
        when(jsonStreamFileWriterUtil.open(fileName)).thenReturn(Mono.just(jsonGenerator));
        when(jsonStreamFileWriterUtil.write(any(), any())).thenReturn(Mono.empty());
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.just(singlePage));

        Mono<Void> result = clientDataService.fetchAll(url, fileName);
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(webClient, times(1)).get();
    }

    @Test
    @DisplayName("Should fetch multiple pages when next page exists")
    void fetchAll_shouldFetchMultiplePages_whenNextPageExists() throws IOException {
        JsonNode page1 = jsonFromFile("/page_1.json");
        JsonNode page2 = jsonFromFile("/page_2.json");
        JsonNode finalPage = jsonFromFile("/page_final.json");
        URI url = URI.create("https://api.example.com/2.3.0/launches");
        String fileName = tempDir.resolve("output.json").toString();

        when(throttleService.fetchThrottle()).thenReturn(Mono.just(responseZeroUseSeconds));
        when(jsonStreamFileWriterUtil.open(fileName)).thenReturn(Mono.just(jsonGenerator));
        when(jsonStreamFileWriterUtil.write(any(), any())).thenReturn(Mono.empty());

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.just(page1))
                .thenReturn(Mono.just(page2))
                .thenReturn(Mono.just(finalPage));

        Mono<Void> result = clientDataService.fetchAll(url, fileName);
        StepVerifier.create(result)
                .expectComplete()
                .verify(Duration.ofSeconds(5));

        verify(webClient, times(3)).get();
    }

    @Test
    @DisplayName("Should applyStrategy throttle delay when throttle service returns delay")
    void fetchAll_shouldApplyThrottle_whenDelayIsProvided() throws IOException {
        // given
        JsonNode singlePage = jsonFromFile("/page_final.json");
        URI url = URI.create("https://api.example.com/data");
        String fileName = tempDir.resolve("output.json").toString();

        //when
        when(throttleService.fetchThrottle()).thenReturn(Mono.just(responseUseSeconds));
        when(jsonStreamFileWriterUtil.open(fileName)).thenReturn(Mono.just(jsonGenerator));
        when(jsonStreamFileWriterUtil.write(any(), any())).thenReturn(Mono.empty());

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.just(singlePage));
        //then
        Mono<Void> result = clientDataService.fetchAll(url, fileName);

        StepVerifier.create(result)
                .expectComplete()
                .verify(Duration.ofSeconds(100L));

        verify(throttleService, atLeastOnce()).fetchThrottle();
    }

    @Test
    @DisplayName("Should retry and continue when rate limit exception occurs")
    void fetch_shouldRetry_onRateLimitException() throws IOException {
        // given
        JsonNode page1 = jsonFromFile("/page_1.json");
        JsonNode finalPage = jsonFromFile("/page_final.json");
        URI url = URI.create("https://api.example.com/data");

        // when
        when(throttleService.fetchThrottle()).thenReturn(Mono.just(responseUseSeconds));
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.error(new RateLimitExceededException("Rate limit hit", 1L)))
                .thenReturn(Mono.just(page1))
                .thenReturn(Mono.just(finalPage));
        // then
        Mono<JsonNode> result = clientDataService.fetch(url);

        StepVerifier.create(result)
                .verifyErrorMatches(error -> error instanceof RateLimitExceededException);
        verify(webClient, times(1)).get();
        verify(throttleService, times(30)).fetchThrottle();
    }

    @Test
    @DisplayName("Should retry and fail when rate limit occurs and delay is zero")
    void fetch_shouldRetry_onRateLimitExceptionAndDelayIsZero() throws IOException {
        // given
        JsonNode page1 = jsonFromFile("/page_1.json");
        JsonNode finalPage = jsonFromFile("/page_final.json");
        URI url = URI.create("https://api.example.com/data");
        // when
        when(throttleService.fetchThrottle()).thenReturn(Mono.just(responseZeroUseSeconds));
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.error(new RateLimitExceededException("Rate limit hit", 0L)))
                .thenReturn(Mono.just(page1))
                .thenReturn(Mono.just(finalPage));
        // then
        Mono<JsonNode> result = clientDataService.fetch(url);
        StepVerifier.create(result)
                .verifyError();

        verify(webClient, times(1)).get();
        verify(throttleService, times(30)).fetchThrottle();
    }

    @Test
    @DisplayName("Should fetch all pages when pagination spans multiple requests")
    void fetchNext_shouldFetchAllPages_whenMultiplePagesExist() throws IOException {
        JsonNode page1 = jsonFromFile("/page_1.json");
        JsonNode page2 = jsonFromFile("/page_2.json");
        JsonNode page3 = jsonFromFile("/page_final.json");

        URI url = URI.create("https://api.example.com/launches?page=1");
        String fileName = tempDir.resolve("output.json").toString();

        when(throttleService.fetchThrottle()).thenReturn(Mono.just(responseZeroUseSeconds));
        when(jsonStreamFileWriterUtil.write(any(), any())).thenReturn(Mono.empty());

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.just(page1))
                .thenReturn(Mono.just(page2))
                .thenReturn(Mono.just(page3));

        StepVerifier.create(clientDataService.fetchNext(url, fileName, jsonGenerator))
                .expectComplete()
                .verify();

        verify(webClient, times(3)).get();
        verify(jsonStreamFileWriterUtil, times(3)).write(any(), any());
    }

    @Test
    @DisplayName("Should complete immediately when next page URL is null")
    void fetchNext_shouldReturnEmpty_whenUrlIsNull() {

        StepVerifier.create(clientDataService.fetchNext(null, "test.json", jsonGenerator))
                .verifyComplete();

        verify(webClient, never()).get();
    }
}
