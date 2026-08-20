package com.moonkeyeu.etl.api.integration.s3;

import com.moonkeyeu.etl.api.config.S3TestConfig;
import com.moonkeyeu.etl.api.configuration.caching.CacheConfig;
import com.moonkeyeu.etl.api.configuration.client.WebClientConfig;
import com.moonkeyeu.etl.api.pipeline.ll2.media.MediaTarget;
import com.moonkeyeu.etl.api.pipeline.ll2.media.PendingImage;
import com.moonkeyeu.etl.api.service.S3CrudService;
import com.moonkeyeu.etl.api.service.S3MediaService;
import com.moonkeyeu.etl.api.service.impl.MediaDownloadServiceImpl;
import com.moonkeyeu.etl.api.service.impl.s3.S3CrudServiceImpl;
import com.moonkeyeu.etl.api.service.impl.s3.S3MediaServiceImpl;
import com.moonkeyeu.etl.api.service.impl.s3.S3StorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(
        classes = {
                WebClientConfig.class,
                S3TestConfig.class,
                CacheConfig.class,
                S3MediaServiceImpl.class,
                S3CrudServiceImpl.class,
                S3StorageServiceImpl.class,
                MediaDownloadServiceImpl.class,
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@Testcontainers
@DisplayName("S3MediaServiceImplTest Integration tests")
class S3MediaServiceImplIT {

    @Autowired
    private S3MediaService s3MediaService;
    @Autowired
    private S3CrudService s3CrudService;
    private PendingImage rocketImageEntity;
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

        rocketImageEntity = new PendingImage(MediaTarget.ROCKET_CONF_IMAGES, 1L, null);
        rocketImageEntity.setImageUrl(sourceImageUrl);
    }

    @Test
    @DisplayName("Should save media to S3 if absent and return CloudFront URL")
    void shouldSaveMediaToS3_ifAbsentReturnCloudFrontUrl() throws IOException {
        String key = "media/images/rockets/falcon_9_image_20230807133459.jpeg";
        s3CrudService.deleteObject(BUCKET, key);
        String cdnUrl = s3MediaService.saveMediaToS3(rocketImageEntity, BUCKET);
        assertNotNull(cdnUrl);
        assertEquals(cdnImageUrl, cdnUrl);
    }

    @Test
    @DisplayName("Should return CloudFront URL")
    void shouldReturnCloudFrontUrl() throws IOException {
        String cdnUrl = s3MediaService.getCloudFrontUrl(rocketImageEntity);
        assertNotNull(cdnUrl);
        assertEquals(cdnImageUrl, cdnUrl);
    }
}