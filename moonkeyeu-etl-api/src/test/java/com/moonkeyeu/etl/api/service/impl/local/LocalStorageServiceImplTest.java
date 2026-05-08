package com.moonkeyeu.etl.api.service.impl.local;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalStorageServiceImplTest {

    @InjectMocks
    private LocalStorageServiceImpl localStorageService;

    @Mock
    private OutputStream outputStream;

    @Test
    @DisplayName("Should return true when file exists")
    void existsByKey_shouldReturnTrue_whenFileExists() {

        Path filePath = Path.of("storage/image.png");

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            filesMock.when(() -> Files.exists(filePath))
                    .thenReturn(true);

            boolean result = localStorageService.existsByKey(filePath);

            assertThat(result).isTrue();
        }
    }

    @Test
    @DisplayName("Should return false when file does not exist")
    void existsByKey_shouldReturnFalse_whenFileDoesNotExist() {

        Path filePath = Path.of("storage/missing.png");

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            filesMock.when(() -> Files.exists(filePath))
                    .thenReturn(false);

            boolean result = localStorageService.existsByKey(filePath);

            assertThat(result).isFalse();
        }
    }

    @Test
    @DisplayName("Should save file successfully")
    void save_shouldWriteDataSuccessfully() throws Exception {

        Path filePath = Path.of("storage/image.png");

        byte[] data = "image-content".getBytes();

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            filesMock.when(() -> Files.newOutputStream(filePath))
                    .thenReturn(outputStream);

            localStorageService.save(data, filePath);

            verify(outputStream).write(data);
            verify(outputStream).close();
        }
    }

    @Test
    @DisplayName("Should throw RuntimeException when saving file fails")
    void save_shouldThrowRuntimeException_whenIOExceptionOccurs() {

        Path filePath = Path.of("storage/image.png");

        byte[] data = "image-content".getBytes();

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            filesMock.when(() -> Files.newOutputStream(filePath))
                    .thenThrow(new IOException("Disk is full"));

            assertThatThrownBy(() ->
                    localStorageService.save(data, filePath))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Failed to save image: Disk is full")
                    .hasCauseInstanceOf(IOException.class);
        }
    }

    @Test
    @DisplayName("Should throw RuntimeException when write operation fails")
    void save_shouldThrowRuntimeException_whenWriteFails() throws Exception {

        Path filePath = Path.of("storage/image.png");

        byte[] data = "image-content".getBytes();

        doThrow(new IOException("Write failed"))
                .when(outputStream)
                .write(data);

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            filesMock.when(() -> Files.newOutputStream(filePath))
                    .thenReturn(outputStream);

            assertThatThrownBy(() ->
                    localStorageService.save(data, filePath))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Failed to save image: Write failed")
                    .hasCauseInstanceOf(IOException.class);
        }
    }
}