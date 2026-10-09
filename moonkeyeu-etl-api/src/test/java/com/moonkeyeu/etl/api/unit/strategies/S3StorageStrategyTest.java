package com.moonkeyeu.etl.api.unit.strategies;

import com.moonkeyeu.etl.api.config.TestEntity;
import com.moonkeyeu.etl.api.configuration.s3.S3Buckets;
import com.moonkeyeu.etl.api.service.S3MediaService;
import com.moonkeyeu.etl.api.strategy.S3StorageStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3StorageStrategyTest {
    @Mock
    private S3MediaService s3MediaService;
    @Mock
    private S3Buckets s3Buckets;
    @InjectMocks
    private S3StorageStrategy s3StorageStrategy;

    @Test
    @DisplayName("Should upload image to S3 and return the cloudFrontUrl")
    void ShouldGetCloudFrontURL_whenS3BucketAndMediaEntityAreNotNull() throws IOException {
        // given
        TestEntity entity = new TestEntity("1", "test");
        when(s3MediaService.saveMediaToS3(entity, s3Buckets.getBucketName()))
                .thenReturn("https://image.com/test.png");
        // when
        String imageUrl = s3StorageStrategy.save(entity);

        // then
        assertNotNull(imageUrl);
        assertEquals("https://image.com/test.png", imageUrl);

        verify(s3MediaService).saveMediaToS3(entity, s3Buckets.getBucketName());
    }

    @Test
    @DisplayName("Should return cloudFrontUrl when image entity is not null")
    void shouldGetCloudFrontURL_whenMediaEntityIsNotNull() throws MalformedURLException {
        // given
        TestEntity entity = new TestEntity("1", "test");
        when(s3MediaService.getCloudFrontUrl(entity))
                .thenReturn("https://image.com/test.png");
        // when
        String imageUrl = s3StorageStrategy.getUrl(entity);

        // thn
        assertNotNull(imageUrl);
        assertEquals("https://image.com/test.png", imageUrl);

        verify(s3MediaService).getCloudFrontUrl(entity);
    }
}