package com.moonkeyeu.etl.api.service.client;

import com.moonkeyeu.etl.api.service.s3.S3Service;
import com.moonkeyeu.etl.api.utils.ClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.net.MalformedURLException;

@Slf4j
@Service
public class ClientS3CloudService {
    @Value("${aws.cloudfront.url}")
    private String cloudFrontUrl;
    private final WebClient webClient;
    private final S3Service s3Service;
    private final CacheManager cacheManager;


    @Autowired
    public ClientS3CloudService(WebClient webClient, S3Service s3Service, CacheManager cacheManager) {
        this.webClient = webClient;
        this.s3Service = s3Service;
        this.cacheManager = cacheManager;
    }

    public String saveToS3(String imageUrl, String bucketName, String key, boolean skipUpload) throws IOException {
        try {
            String fileName = ClientUtils.extractImageNameFromURL(imageUrl);
            String s3Root = key + fileName;

            if (skipUpload) {
                // log.info("hasAlreadyUploaded: " +  skipUpload + " Image already exists in S3: " + cloudFrontUrl + s3Root);
                return cloudFrontUrl + s3Root;
            }

            Cache cache = cacheManager.getCache("processedImages");
            if (cache != null) {
                Boolean exists = cache.get(s3Root, Boolean.class);
                if (exists != null) {
                    return cloudFrontUrl + s3Root;
                }
            }

            boolean existsInS3 = s3Service.isObjectExists(bucketName, s3Root);
            if (existsInS3) {
                //log.info("Image already exists in S3: " + cloudFrontUrl + s3Root);
                return cloudFrontUrl + s3Root;

            }

            if (cache != null) {
                // log.info("Cache: New image root added - " + cloudFrontUrl + s3Root);
                cache.put(s3Root, existsInS3);
            }

            imageWebClient(imageUrl)
                    .flatMap(dataBuffer -> uploadToS3(bucketName, s3Root, dataBuffer))
                    .doOnSuccess(result -> log.info("Uploaded image to S3: " + fileName))
                    .doOnError(e -> log.error("Error during processing: " + e.getMessage()))
                    .block();

            log.info("new url: {}", cloudFrontUrl + s3Root);
            return cloudFrontUrl + s3Root;
        } catch (MalformedURLException e) {
            log.error("Invalid URL generated: " + imageUrl, e);
            throw new MalformedURLException("Failed to save image to S3: " + imageUrl);
        } catch (Exception e) {
            log.error("Unexpected error occurred: " + e.getMessage(), e);
            throw new IOException("Failed to save image to S3: " + imageUrl, e);
        }
    }

    public Mono<Void> uploadToS3(String bucketName, String key, DataBuffer dataBuffer) {
        return Mono.fromRunnable(() -> {
            try {
                RequestBody requestBody = RequestBody.fromInputStream(dataBuffer.asInputStream(true), dataBuffer.readableByteCount());
                s3Service.putObject(bucketName, key, requestBody);
            } finally {
                DataBufferUtils.release(dataBuffer);
            }
        });
    }

    private Mono<DataBuffer> imageWebClient(String imageUrl) {
        return webClient.get()
                .uri(imageUrl)
                .retrieve()
                .bodyToMono(DataBuffer.class)
                .doOnError(e -> log.error("Error downloading image: " + e.getMessage()));
    }
}
