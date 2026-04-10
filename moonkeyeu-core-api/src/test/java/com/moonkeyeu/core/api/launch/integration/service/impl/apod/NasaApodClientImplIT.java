package com.moonkeyeu.core.api.launch.integration.service.impl.apod;

import com.moonkeyeu.core.api.launch.dto.NasaApodDTO;
import com.moonkeyeu.core.api.launch.services.NasaApodClientService;
import com.moonkeyeu.core.api.launch.services.impl.apod.NasaApodClientServiceImpl;
import com.moonkeyeu.core.api.settings.exceptions.NasaApodFetchException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration testing utility using MockWebServer.
 *
 * <p>The Spring team recommends using MockWebServer for writing integration tests that involve
 * WebClient or other HTTP clients. It allows you to simulate HTTP responses without calling
 * external services.
 *
 * <p>Reference: <a href="https://github.com/spring-projects/spring-framework/issues/19852#issuecomment-453452354">
 * Spring Framework GitHub Issue #19852</a>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NasaApodClientIntegrationImplTest Integration Tests")
public class NasaApodClientImplIT {
    public static MockWebServer mockBackEnd;
    public NasaApodClientService nasaApodClientService;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @BeforeEach
    void setup() {
        String baseUrl = mockBackEnd.url("/").toString();

        nasaApodClientService = new NasaApodClientServiceImpl(
                WebClient.builder(),
                baseUrl
        );

        ReflectionTestUtils.setField(nasaApodClientService, "apiKey", "test-key");
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Should Return the Latest NASA Astronomy Picture of the Day")
    void shouldFetchApodSuccessfully() throws Exception {
        String mockBody = """
                {
                  "title": "Test Title",
                  "explanation": "Test Explanation",
                  "url": "https://example.com/image.jpg"
                }
                """;

        mockBackEnd.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(mockBody)
                        .addHeader("Content-Type", "application/json")
        );

        NasaApodDTO result = nasaApodClientService.fetchNasaAstronomyPictureOfTheDay();
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Title");

        RecordedRequest request = mockBackEnd.takeRequest();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getRequestUrl().queryParameter("api_key"))
                .isEqualTo("test-key");
    }

    @Test
    @DisplayName("Should Handle Successfully Errors 5xx")
    void shouldThrowExceptionOnError5XX() {
        mockBackEnd.enqueue(
                new MockResponse()
                        .setResponseCode(500)
                        .setBody("Internal Server Error")
        );

        assertThatThrownBy(() ->
                nasaApodClientService.fetchNasaAstronomyPictureOfTheDay()
        ).isInstanceOf(NasaApodFetchException.class).hasMessageContaining("Internal Server Error");
    }

    @Test
    @DisplayName("Should Handle Successfully Errors 4xx")
    void shouldThrowExceptionOnError4XX() {
        mockBackEnd.enqueue(
                new MockResponse()
                        .setResponseCode(400)
                        .setBody("Bad Request")
        );

        assertThatThrownBy(() ->
                nasaApodClientService.fetchNasaAstronomyPictureOfTheDay()
        ).isInstanceOf(NasaApodFetchException.class).hasMessageContaining("Bad Request");
    }
}
