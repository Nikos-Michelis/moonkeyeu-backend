package com.moonkeyeu.etl.api.unit.service.s3;

import com.moonkeyeu.etl.api.service.S3CrudService;
import com.moonkeyeu.etl.api.service.impl.s3.S3StorageServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import software.amazon.awssdk.core.sync.RequestBody;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3StorageServiceImplTest {

    @Mock
    private S3CrudService s3CrudService;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache cache;
    @InjectMocks
    private S3StorageServiceImpl s3StorageService;

    @Test
    @DisplayName("Should upload file and cache processed image")
    void save_shouldUploadAndCacheImage() {

        byte[] data = "test-data".getBytes();
        String s3Key = "images/test.jpg";
        String bucketName = "test-bucket";

        when(cacheManager.getCache("processedImages"))
                .thenReturn(cache);

        s3StorageService.save(data, s3Key, bucketName);

        verify(s3CrudService).putObject(eq(bucketName), eq(s3Key), any(RequestBody.class));
        verify(cache).put(s3Key, true);
    }

    @Test
    @DisplayName("Should upload file even when cache is null")
    void save_shouldUpload_whenCacheIsNull() {

        byte[] data = "test-data".getBytes();
        String s3Key = "images/test.jpg";
        String bucketName = "test-bucket";

        when(cacheManager.getCache("processedImages"))
                .thenReturn(null);

        s3StorageService.save(data, s3Key, bucketName);

        verify(s3CrudService).putObject(eq(bucketName), eq(s3Key), any(RequestBody.class));
        verifyNoInteractions(cache);
    }

    @Test
    @DisplayName("Should return true when object exists in cache")
    void existsByKey_shouldReturnTrue_whenExistsInCache() {

        String s3Key = "images/test.jpg";
        String bucketName = "test-bucket";

        when(cacheManager.getCache("processedImages"))
                .thenReturn(cache);
        when(cache.get(s3Key, Boolean.class))
                .thenReturn(true);

        boolean result = s3StorageService.existsByKey(s3Key, bucketName);

        assertThat(result).isTrue();
        verifyNoInteractions(s3CrudService);
    }

    @Test
    @DisplayName("Should return true and cache result when object exists in S3")
    void existsByKey_shouldReturnTrueAndCache_whenExistsInS3() {

        String s3Key = "images/test.jpg";
        String bucketName = "test-bucket";

        when(cacheManager.getCache("processedImages"))
                .thenReturn(cache);
        when(cache.get(s3Key, Boolean.class))
                .thenReturn(null);
        when(s3CrudService.isObjectExists(bucketName, s3Key))
                .thenReturn(true);

        boolean result = s3StorageService.existsByKey(s3Key, bucketName);

        assertThat(result).isTrue();

        verify(cache)
                .put(s3Key, true);
    }

    @Test
    @DisplayName("Should return false when object does not exist in cache or S3")
    void existsByKey_shouldReturnFalse_whenObjectDoesNotExist() {

        String s3Key = "images/test.jpg";
        String bucketName = "test-bucket";

        when(cacheManager.getCache("processedImages"))
                .thenReturn(cache);
        when(cache.get(s3Key, Boolean.class))
                .thenReturn(null);
        when(s3CrudService.isObjectExists(bucketName, s3Key))
                .thenReturn(false);

        boolean result = s3StorageService.existsByKey(s3Key, bucketName);

        assertThat(result).isFalse();

        verify(cache, never())
                .put(anyString(), any());
    }

    @Test
    @DisplayName("Should check S3 when cache is null")
    void existsByKey_shouldCheckS3_whenCacheIsNull() {

        String s3Key = "images/test.jpg";
        String bucketName = "test-bucket";

        when(cacheManager.getCache("processedImages"))
                .thenReturn(null);
        when(s3CrudService.isObjectExists(bucketName, s3Key))
                .thenReturn(true);

        boolean result = s3StorageService.existsByKey(s3Key, bucketName);

        assertThat(result).isTrue();

        verify(s3CrudService)
                .isObjectExists(bucketName, s3Key);
    }

    @Test
    @DisplayName("Should upload object successfully")
    void upload_shouldUploadSuccessfully() {

        byte[] data = "content".getBytes();
        String s3Key = "folder/file.txt";
        String bucketName = "bucket";

        s3StorageService.upload(s3Key, bucketName, data);

        verify(s3CrudService)
                .putObject(
                        eq(bucketName),
                        eq(s3Key),
                        any(RequestBody.class)
                );
    }

    @Test
    @DisplayName("Should throw RuntimeException when upload fails")
    void upload_shouldThrowRuntimeException_whenUploadFails() {

        byte[] data = "content".getBytes();
        String s3Key = "folder/file.txt";
        String bucketName = "bucket";

        doThrow(new RuntimeException("S3 failure"))
                .when(s3CrudService)
                .putObject(
                        eq(bucketName),
                        eq(s3Key),
                        any(RequestBody.class)
                );

        assertThatThrownBy(() ->
                s3StorageService.upload(s3Key, bucketName, data))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to upload folder/file.txt to S3")
                .hasCauseInstanceOf(RuntimeException.class);
    }
}