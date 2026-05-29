package com.moonkeyeu.etl.api.unit.service.s3;

import com.moonkeyeu.etl.api.model.images.RocketImageEntity;
import com.moonkeyeu.etl.api.service.S3StorageService;
import com.moonkeyeu.etl.api.service.impl.MediaDownloadServiceImpl;
import com.moonkeyeu.etl.api.service.impl.s3.S3MediaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3MediaServiceImplTest {

    @Mock
    private S3StorageService s3StorageService;
    @Mock
    private MediaDownloadServiceImpl mediaDownloadService;
    @InjectMocks
    private S3MediaServiceImpl s3MediaService;
    private RocketImageEntity imageEntity;

    @BeforeEach
    void setUp() {
        imageEntity = new RocketImageEntity();
        ReflectionTestUtils.setField(s3MediaService, "s3KeyValue", "media");
        ReflectionTestUtils.setField(s3MediaService, "cloudFrontUrl", "https://cdn.test.com");
    }

    @Test
    @DisplayName("Should return existing CloudFront URL when media already exists in S3")
    void saveMediaToS3_shouldReturnCloudFrontUrl_whenObjectAlreadyExists() throws IOException {
        imageEntity.setImageUrl("https://images.test.com/falcon9.png");

        when(s3StorageService.existsByKey("media/rockets/falcon9.png", "bucket-name"))
                .thenReturn(true);

        String result = s3MediaService.saveMediaToS3(imageEntity, "bucket-name");

        assertThat(result).isEqualTo("https://cdn.test.com/media/rockets/falcon9.png");

        verify(mediaDownloadService, never()).download(anyString());
        verify(s3StorageService, never()).save(any(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should download and save media to S3 when object does not exist")
    void saveMediaToS3_shouldDownloadAndSave_whenObjectDoesNotExist() throws IOException {

        imageEntity.setImageUrl("https://images.test.com/starship.jpg");

        byte[] data = "image-content".getBytes();

        when(s3StorageService.existsByKey("media/rockets/starship.jpg", "bucket-name"))
                .thenReturn(false);

        when(mediaDownloadService.download(imageEntity.getImageUrl()))
                .thenReturn(data);

        String result = s3MediaService.saveMediaToS3(imageEntity, "bucket-name");

        assertThat(result)
                .isEqualTo("https://cdn.test.com/media/rockets/starship.jpg");

        verify(mediaDownloadService)
                .download(imageEntity.getImageUrl());
        verify(s3StorageService)
                .save(data, "media/rockets/starship.jpg", "bucket-name");
    }

    @Test
    @DisplayName("Should build correct CloudFront URL")
    void getCloudFrontUrl_shouldReturnCorrectUrl() throws Exception {

        RocketImageEntity entity = new RocketImageEntity();
        entity.setImageUrl("https://images.test.com/falcon-heavy.png");

        String result = s3MediaService.getCloudFrontUrl(entity);

        assertThat(result).isEqualTo("https://cdn.test.com/media/rockets/falcon-heavy.png");
    }

    @Test
    @DisplayName("Should throw exception when image URL is invalid")
    void saveMediaToS3_shouldThrowException_whenImageUrlIsInvalid() {

        imageEntity.setImageUrl("invalid-url");

        assertThatThrownBy(() -> s3MediaService.saveMediaToS3(imageEntity, "bucket-name"))
                .isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("Should use correct S3 path for RocketImageEntity")
    void getCloudFrontUrl_shouldUseCorrectRootPath() throws Exception {

        imageEntity.setImageUrl("https://images.test.com/test-image.webp");

        String result = s3MediaService.getCloudFrontUrl(imageEntity);

        assertThat(result)
                .contains("/media/rockets/test-image.webp");
    }
}