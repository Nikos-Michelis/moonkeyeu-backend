package com.moonkeyeu.etl.api.configuration.batch.processors;

import com.moonkeyeu.etl.api.config.TestEntity;
import com.moonkeyeu.etl.api.configuration.files.FilePathProvider;
import com.moonkeyeu.etl.api.configuration.files.RootConfig;
import com.moonkeyeu.etl.api.configuration.s3.S3Buckets;
import com.moonkeyeu.etl.api.dto.storage.StorageType;
import com.moonkeyeu.etl.api.dto.storage.StoreOperation;
import com.moonkeyeu.etl.api.service.LocalMediaService;
import com.moonkeyeu.etl.api.service.S3MediaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class MediaProcessorTest {

    @Mock
    private LocalMediaService localMediaService;
    @Mock
    private S3MediaService s3MediaService;
    @Mock
    private RootConfig rootConfig;
    @Mock
    private FilePathProvider filePathProvider;
    @Mock
    private S3Buckets s3Buckets;
    @InjectMocks
    private MediaProcessor mediaProcessor;


    @Test
    @DisplayName("Should return null when primary key is null")
    void shouldReturnNull_whenPrimaryKeyIsNull() throws Exception {

        TestEntity entity = new TestEntity(null, "test");

        var result = mediaProcessor.process(entity);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should save image using LOCAL_STORAGE + LOCAL_SAVE")
    void shouldSaveToLocalStorage() throws Exception {
        mediaProcessor.setStorage(StorageType.LOCAL_STORAGE.name());
        mediaProcessor.setOperation(StoreOperation.LOCAL_SAVE.name());
        TestEntity entity = new TestEntity("1", "test");

        when(rootConfig.getImagesRootFolder()).thenReturn("root");
        when(filePathProvider.getImagesDir("root")).thenReturn("images");

        when(localMediaService.saveMediaLocal(entity, "images"))
                .thenReturn("local-url");

        var result = mediaProcessor.process(entity);

        assertNotNull(result);
        assertThat(((TestEntity) result).getImageUrl())
                .isEqualTo("local-url");

        verify(localMediaService)
                .saveMediaLocal(entity, "images");
    }

    @Test
    @DisplayName("Should return LOCAL URL when GET_URL operation")
    void shouldGetLocalUrl() throws Exception {
        mediaProcessor.setStorage(StorageType.LOCAL_STORAGE.name());
        mediaProcessor.setOperation(StoreOperation.GET_URL.name());
        TestEntity entity = new TestEntity("2", "test");

        when(localMediaService.getLocalHostUrl(entity))
                .thenReturn("local-url");

        var result = mediaProcessor.process(entity);

        assertThat(((TestEntity) result).getImageUrl())
                .isEqualTo("local-url");

        verify(localMediaService)
                .getLocalHostUrl(entity);
    }

    @Test
    @DisplayName("Should upload image to S3")
    void shouldUploadToS3() throws Exception {
        mediaProcessor.setStorage(StorageType.S3_STORAGE.name());
        mediaProcessor.setOperation(StoreOperation.S3_UPLOAD.name());
        TestEntity entity = new TestEntity("3", "test");

        when(s3Buckets.getBucketName()).thenReturn("bucket");

        when(s3MediaService.saveMediaToS3(entity, "bucket"))
                .thenReturn("s3-url");

        var result = mediaProcessor.process(entity);

        assertThat(((TestEntity) result).getImageUrl())
                .isEqualTo("s3-url");

        verify(s3MediaService)
                .saveMediaToS3(entity, "bucket");
    }

    @Test
    @DisplayName("Should return CloudFront URL from S3 service")
    void shouldGetS3Url() throws Exception {
        mediaProcessor.setStorage(StorageType.S3_STORAGE.name());
        mediaProcessor.setOperation(StoreOperation.GET_URL.name());
        TestEntity entity = new TestEntity("4", "test");

        when(s3MediaService.getCloudFrontUrl(entity))
                .thenReturn("cloudfront-url");

        var result = mediaProcessor.process(entity);

        assertThat(((TestEntity) result).getImageUrl())
                .isEqualTo("cloudfront-url");

        verify(s3MediaService)
                .getCloudFrontUrl(entity);
    }

    @Test
    @DisplayName("Should throw exception when storage is invalid")
    void shouldThrowException_whenStorageInvalid() {

        TestEntity entity = new TestEntity("3", "test");

        assertThatThrownBy(() -> mediaProcessor.process(entity))
                .isInstanceOf(Exception.class);
    }
}