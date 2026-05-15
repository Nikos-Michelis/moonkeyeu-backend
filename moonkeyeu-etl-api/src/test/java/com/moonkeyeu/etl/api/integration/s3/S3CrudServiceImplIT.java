package com.moonkeyeu.etl.api.integration.s3;

import com.adobe.testing.s3mock.testcontainers.S3MockContainer;
import com.moonkeyeu.etl.api.config.TestContainerConfiguration;
import com.moonkeyeu.etl.api.service.S3CrudService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Testcontainers
@DisplayName("S3CrudServiceImpl Integration tests")
class S3CrudServiceImplIT {

    @Container
    private static final S3MockContainer s3Mock = new S3MockContainer("latest");

    @Value("${aws.s3.buckets.bucket-name}")
    private String BUCKET;
    @Autowired
    private S3CrudService s3CrudService;
    @Autowired
    private S3Client s3Client;

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
    @DisplayName("Should upload object to S3 and verify its existence")
    void shouldUploadObject() {
        String key = "folder/test.txt";
        s3CrudService.putObject(BUCKET, key, RequestBody.fromString("hello world"));
        boolean exists = s3CrudService.isObjectExists(BUCKET, key);
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should delete object from S3 and verify it no longer exists")
    void shouldDeleteObject() {
        String key = "folder/test.txt";
        s3CrudService.deleteObject(BUCKET, key);
        boolean exists = s3CrudService.isObjectExists(BUCKET, key);
        assertFalse(exists);
    }

    @Test
    @DisplayName("Should return false when checking if non-existent object exists")
    void shouldReturnFalseWhenObjectDoesNotExist() {
        boolean exists =
                s3CrudService.isObjectExists(BUCKET, "missing.txt");
        assertFalse(exists);
    }
}