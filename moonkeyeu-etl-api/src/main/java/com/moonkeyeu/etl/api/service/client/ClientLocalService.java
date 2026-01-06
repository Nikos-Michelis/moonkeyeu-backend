package com.moonkeyeu.etl.api.service.client;

import com.moonkeyeu.etl.api.utils.ClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class ClientLocalService {
    private final WebClient webClient;

    @Autowired
    public ClientLocalService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String saveLocal(String imageUrl, String localDir, String baseUrl) throws IOException {
        try {
            String fileName = ClientUtils.extractImageNameFromURL(imageUrl);

            Path filePath = Paths.get(localDir, fileName);
            if (Files.exists(filePath)) {
                log.info("Image already exists: " + filePath);
                return baseUrl + fileName;
            }
            Files.createDirectories(Paths.get(localDir));
            imageWebClient(imageUrl)
                    .flatMap(dataBuffer -> saveToLocalStorage(dataBuffer, filePath))
                    .doOnSuccess(result -> log.info("Image processed and saved successfully"))
                    .doOnError(e -> log.error("Error during processing: " + e.getMessage()))
                    .block();

            return baseUrl + fileName;
        } catch (IOException e) {
            throw new IOException("Failed to download image: " + imageUrl, e);
        }
    }

    private Mono<DataBuffer> imageWebClient(String imageUrl) {
        return webClient.get()
                .uri(imageUrl)
                .retrieve()
                .bodyToMono(DataBuffer.class)
                .doOnError(e -> log.error("Error downloading image: " + e.getMessage()));
    }

    public Mono<Path> saveToLocalStorage(DataBuffer dataBuffer, Path filePath){
        try {
            ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            Files.write(filePath, bytes);
            log.info("Downloaded and saved image: " + filePath);
        } catch (IOException e) {
            return Mono.error(new RuntimeException("Failed to save image: " + e.getMessage(), e));
        } finally {
            DataBufferUtils.release(dataBuffer);
        }
        return Mono.just(filePath);
    }
}
