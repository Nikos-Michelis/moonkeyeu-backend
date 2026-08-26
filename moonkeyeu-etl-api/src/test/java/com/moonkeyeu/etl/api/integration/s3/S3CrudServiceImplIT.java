package com.moonkeyeu.etl.api.integration.s3;

import com.moonkeyeu.etl.api.config.S3TestContainerConfig;
import com.moonkeyeu.etl.api.configuration.caching.CacheConfig;
import com.moonkeyeu.etl.api.service.S3CrudService;
import com.moonkeyeu.etl.api.service.impl.s3.S3CrudServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.core.sync.RequestBody;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(
        classes={
                S3CrudServiceImpl.class,
                CacheConfig.class,
        },
        webEnvironment= SpringBootTest.WebEnvironment.NONE
)
@Import(S3TestContainerConfig.class)
@DisplayName("S3CrudServiceImpl Integration tests")
class S3CrudServiceImplIT {

    @Value("${aws.s3.buckets.bucket-name}")
    private String BUCKET;
    @Autowired
    private S3CrudService s3CrudService;
    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void init() {
        cacheManager.getCacheNames()
                .forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
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
