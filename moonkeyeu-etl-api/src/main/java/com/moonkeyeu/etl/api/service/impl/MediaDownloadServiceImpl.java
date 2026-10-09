package com.moonkeyeu.etl.api.service.impl;

import com.moonkeyeu.etl.api.service.MediaDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MediaDownloadServiceImpl implements MediaDownloadService {
    private final WebClient webClient;

    @Override
    public byte[] download(String resourceUrl) throws IOException {
        try {
            DataBuffer dataBuffer = webClient.get()
                    .uri(resourceUrl)
                    .retrieve()
                    .bodyToMono(DataBuffer.class)
                    .block();

            if (dataBuffer == null) {
                throw new IOException("Downloaded resource is empty: " + resourceUrl);
            }

            try (InputStream inputStream = dataBuffer.asInputStream(true)) {
                return inputStream.readAllBytes();
            }

        } catch (Exception e) {
            throw new IOException("Failed to download resource: " + resourceUrl, e);
        }
    }
}
