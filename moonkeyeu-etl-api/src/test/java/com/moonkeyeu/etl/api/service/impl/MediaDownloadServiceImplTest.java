package com.moonkeyeu.etl.api.service.impl;

import com.moonkeyeu.etl.api.dto.ThrottleResponse;
import com.moonkeyeu.etl.api.service.ClientDataService;
import com.moonkeyeu.etl.api.service.impl.client.ClientDataServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaDownloadServiceImplTest {

    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @InjectMocks
    private MediaDownloadServiceImpl mediaDownloadService;
    @Mock
    private DataBuffer dataBuffer;
    private DataBufferFactory bufferFactory;

    @BeforeEach
    void setUp() {
        bufferFactory = new DefaultDataBufferFactory();
    }

    @Test
    @DisplayName("Should download resource successfully")
    void download_shouldReturnBytesSuccessfully() throws Exception {

        String resourceUrl = "https://images.test.com/image.png";
        byte[] expectedBytes = "image-content".getBytes();
        DataBuffer dataBuffer = bufferFactory.wrap(expectedBytes);

        when(webClient.get())
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(resourceUrl))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(DataBuffer.class))
                .thenReturn(Mono.just(dataBuffer));

        byte[] result = mediaDownloadService.download(resourceUrl);

        assertThat(result)
                .isEqualTo(expectedBytes);
    }

    @Test
    @DisplayName("Should throw IOException when downloaded resource is empty")
    void download_shouldThrowIOException_whenDataBufferIsNull() {

        String resourceUrl = "https://images.test.com/image.png";

        when(webClient.get())
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(resourceUrl))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(DataBuffer.class))
                .thenReturn(Mono.empty());

        assertThatThrownBy(() ->
                mediaDownloadService.download(resourceUrl))
                .isInstanceOf(IOException.class)
                .hasMessage("Failed to download resource: " + resourceUrl);
    }

    @Test
    @DisplayName("Should throw IOException when WebClient request fails")
    void download_shouldThrowIOException_whenWebClientFails() {

        String resourceUrl = "https://images.test.com/image.png";

        when(webClient.get())
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(resourceUrl))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(DataBuffer.class))
                .thenReturn(Mono.error(new RuntimeException("Connection failed")));

        assertThatThrownBy(() ->
                mediaDownloadService.download(resourceUrl))
                .isInstanceOf(IOException.class)
                .hasMessage("Failed to download resource: " + resourceUrl)
                        .hasCauseInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should throw IOException when input stream reading fails")
    void download_shouldThrowIOException_whenInputStreamFails() {

        String resourceUrl = "https://images.test.com/image.png";

        when(webClient.get())
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(resourceUrl))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(DataBuffer.class))
                .thenReturn(Mono.just(dataBuffer));
        when(dataBuffer.asInputStream(true))
                .thenThrow(new RuntimeException("Stream failed"));

        assertThatThrownBy(() ->
                mediaDownloadService.download(resourceUrl))
                .isInstanceOf(IOException.class)
                .hasMessage("Failed to download resource: " + resourceUrl);
    }

    @Test
    @DisplayName("Should call WebClient with correct resource URL")
    void download_shouldCallWebClientWithCorrectUrl() throws Exception {

        String resourceUrl = "https://images.test.com/test.jpg";
        byte[] expectedBytes = "content".getBytes();
        DataBuffer dataBuffer = bufferFactory.wrap(expectedBytes);

        when(webClient.get())
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(resourceUrl))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(DataBuffer.class))
                .thenReturn(Mono.just(dataBuffer));

        mediaDownloadService.download(resourceUrl);

        verify(requestHeadersUriSpec)
                .uri(resourceUrl);
        verify(responseSpec)
                .bodyToMono(DataBuffer.class);
    }
}