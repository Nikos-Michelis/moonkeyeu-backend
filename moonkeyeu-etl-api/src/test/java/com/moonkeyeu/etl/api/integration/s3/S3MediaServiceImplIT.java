package com.moonkeyeu.etl.api.integration.s3;

import com.moonkeyeu.etl.api.config.TestContainerConfiguration;
import com.moonkeyeu.etl.api.model.images.RocketImageEntity;
import com.moonkeyeu.etl.api.service.S3CrudService;
import com.moonkeyeu.etl.api.service.S3MediaService;
import com.moonkeyeu.etl.api.service.S3StorageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Testcontainers
@DisplayName("S3MediaServiceImplTest Integration tests")
class S3MediaServiceImplIT {

    @Autowired
    private S3MediaService s3MediaService;
    @Autowired
    private S3CrudService s3CrudService;
    private RocketImageEntity rocketImageEntity;
    @Value("${aws.s3.buckets.bucket-name}")
    private String BUCKET;
    @Value("${aws.cloudfront.url}")
    private String cloudFrontUrl;
    @Value("${application.api.the-space-devs.cdn}")
    private String imageBytesSource;
    private String cdnImageUrl;

    @BeforeEach
    void setup() {
        String sourceImageUrl = UriComponentsBuilder
                .fromUri(URI.create(imageBytesSource))
                .path("falcon_9_image_20230807133459.jpeg")
                .toUriString();
        cdnImageUrl =
                UriComponentsBuilder
                        .fromUri(URI.create(cloudFrontUrl))
                        .path("/media/images/rockets/falcon_9_image_20230807133459.jpeg")
                        .toUriString();

        rocketImageEntity = new RocketImageEntity();
        rocketImageEntity.setImageUrl(sourceImageUrl);
    }

    @Test
    void shouldSaveMediaToS3() throws IOException {
        String key = "media/images/rockets/falcon_9_image_20230807133459.jpeg";
        s3CrudService.deleteObject(BUCKET, key);
        String cdnUrl = s3MediaService.saveMediaToS3(rocketImageEntity, BUCKET);
        assertNotNull(cdnUrl);
        assertEquals(cdnImageUrl, cdnUrl);
    }

    @Test
    void shouldReturnCloudFrontUrl() throws IOException {
        String cdnUrl = s3MediaService.getCloudFrontUrl(rocketImageEntity);
        assertNotNull(cdnUrl);
        assertEquals(cdnImageUrl, cdnUrl);
    }
}