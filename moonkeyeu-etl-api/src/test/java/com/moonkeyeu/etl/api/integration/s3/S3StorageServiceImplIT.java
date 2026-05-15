package com.moonkeyeu.etl.api.integration.s3;

import com.adobe.testing.s3mock.testcontainers.S3MockContainer;
import com.moonkeyeu.etl.api.config.TestContainerConfiguration;
import com.moonkeyeu.etl.api.service.S3StorageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Testcontainers
@DisplayName("S3StorageServiceImpl Integration tests")
class S3StorageServiceImplIT {
    @Autowired
    private S3StorageService s3StorageService;
    @Autowired
    private CacheManager cacheManager;
    @Container
    private static final S3MockContainer s3Mock = new S3MockContainer("latest");
    @Autowired
    private S3Client s3Client;
    @Value("${aws.s3.buckets.bucket-name}")
    private String BUCKET;

    @DynamicPropertySource
    static void configureS3Properties(DynamicPropertyRegistry registry) {
        registry.add("aws.s3.endpoint", s3Mock::getHttpsEndpoint);
        registry.add("aws.s3.region", () -> "eu-central-1");
    }

    @BeforeEach
    void init() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder()
                    .bucket(BUCKET)
                    .build());
            log.info("Bucket {} is ready", BUCKET);
        } catch (Exception e) {
            log.error("Bucket verification failed", e);
            throw e;
        }
    }

    @Test
    void shouldSaveObjectToS3() {
        byte[] bytes = "mock-image-content".getBytes();
        String key = "folder/images/image.png";
        s3StorageService.save(bytes, key, BUCKET);
        byte[] result = s3Client.getObjectAsBytes(
                b -> b.bucket(BUCKET).key(key)
        ).asByteArray();
        assertArrayEquals(bytes, result);
    }

    @Test
    void shouldCheckIfKeyExistsInS3() {
        String key = "folder/images/image.png";
        boolean exists = s3StorageService.existsByKey(key, BUCKET);
        assertTrue(exists);
    }

    @Test
    void shouldCheckIfKeyExistsInCache() {
        // given
        String key = "folder/images/image.png";
        byte[] data = "mock-image".getBytes();
        // when
        s3StorageService.save(data, key, BUCKET);
        Cache cache = cacheManager.getCache("processedImages");
        // then
        assertNotNull(cache);
        Boolean cachedValue = cache.get(key, Boolean.class);
        assertEquals(Boolean.TRUE, cachedValue);
    }
}