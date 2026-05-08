package com.moonkeyeu.etl.api.service.impl.local;

import com.moonkeyeu.etl.api.model.images.RocketImageEntity;
import com.moonkeyeu.etl.api.service.LocalStorageService;
import com.moonkeyeu.etl.api.service.MediaDownloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalMediaServiceImplTest {

    @Mock
    private LocalStorageService localStorageService;

    @Mock
    private MediaDownloadService mediaDownloadService;

    @InjectMocks
    private LocalMediaServiceImpl localMediaService;
    private RocketImageEntity imageEntity;

    @BeforeEach
    void setUp() {
        imageEntity = new RocketImageEntity();
        ReflectionTestUtils.setField(localMediaService, "localHostUrl", "http://localhost:8080/images");
    }

    @Test
    @DisplayName("Should return localhost URL when media already exists locally")
    void saveMediaLocal_shouldReturnLocalUrl_whenFileAlreadyExists() throws IOException {

        imageEntity.setImageUrl("https://images.test.com/falcon9.png");

        String localDir = "storage/rockets";

        Path expectedPath = Paths.get(localDir, "falcon9.png");

        when(localStorageService.existsByKey(expectedPath))
                .thenReturn(true);

        String result = localMediaService.saveMediaLocal(imageEntity, localDir);

        assertThat(result)
                .isEqualTo("http://localhost:8080/images/rockets/falcon9.png");

        verify(mediaDownloadService, never())
                .download(anyString());
        verify(localStorageService, never())
                .save(any(), any());
    }

    @Test
    @DisplayName("Should download and save media locally when file does not exist")
    void saveMediaLocal_shouldDownloadAndSave_whenFileDoesNotExist() throws Exception {

        imageEntity.setImageUrl("https://images.test.com/starship.jpg");
        String localDir = "storage/rockets";
        Path expectedPath = Paths.get(localDir, "starship.jpg");
        byte[] data = "image-content".getBytes();

        when(localStorageService.existsByKey(expectedPath))
                .thenReturn(false);
        when(mediaDownloadService.download(imageEntity.getImageUrl()))
                .thenReturn(data);

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            String result = localMediaService.saveMediaLocal(imageEntity, localDir);
            assertThat(result)
                    .isEqualTo("http://localhost:8080/images/rockets/starship.jpg");
            filesMock.verify(() ->
                    Files.createDirectories(Paths.get(localDir)));

            verify(mediaDownloadService)
                    .download(imageEntity.getImageUrl());
            verify(localStorageService)
                    .save(data, expectedPath);
        }
    }

    @Test
    @DisplayName("Should build correct localhost URL")
    void getLocalHostUrl_shouldReturnCorrectUrl() throws Exception {

        imageEntity.setImageUrl("https://images.test.com/falcon-heavy.webp");
        String result = localMediaService.getLocalHostUrl(imageEntity);

        assertThat(result)
                .isEqualTo("http://localhost:8080/images/rockets/falcon-heavy.webp");
    }

    @Test
    @DisplayName("Should throw exception when image URL is invalid")
    void saveMediaLocal_shouldThrowException_whenImageUrlIsInvalid() {
        imageEntity.setImageUrl("invalid-url");

        assertThatThrownBy(() ->
                localMediaService.saveMediaLocal(imageEntity, "storage/rockets"))
                .isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("Should use correct local path for RocketImageEntity")
    void getLocalHostUrl_shouldUseCorrectRootPath() throws Exception {

        imageEntity.setImageUrl("https://images.test.com/test-image.png");

        String result = localMediaService.getLocalHostUrl(imageEntity);

        assertThat(result)
                .contains("/rockets/test-image.png");
    }

    @Test
    @DisplayName("Should propagate exception when download fails")
    void saveMediaLocal_shouldThrowException_whenDownloadFails() throws Exception {

        imageEntity.setImageUrl("https://images.test.com/image.png");
        String localDir = "storage/rockets";
        Path expectedPath = Paths.get(localDir, "image.png");

        when(localStorageService.existsByKey(expectedPath))
                .thenReturn(false);
        when(mediaDownloadService.download(imageEntity.getImageUrl()))
                .thenThrow(new IOException("Download failed"));

        assertThatThrownBy(() ->
                localMediaService.saveMediaLocal(imageEntity, localDir))
                .isInstanceOf(IOException.class)
                .hasMessage("Download failed");

    }
}