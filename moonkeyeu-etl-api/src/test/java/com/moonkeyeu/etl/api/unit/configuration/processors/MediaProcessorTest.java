package com.moonkeyeu.etl.api.unit.configuration.processors;

import com.moonkeyeu.etl.api.config.TestEntity;
import com.moonkeyeu.etl.api.configuration.batch.processors.MediaProcessor;
import com.moonkeyeu.etl.api.dto.storage.StorageType;
import com.moonkeyeu.etl.api.dto.storage.StoreOperation;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.launch.LaunchEntity;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreOperationException;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreProviderException;
import com.moonkeyeu.etl.api.strategy.registry.StorageOperationRegistry;
import com.moonkeyeu.etl.api.strategy.StorageStrategy;
import com.moonkeyeu.etl.api.strategy.registry.StorageStrategyRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class MediaProcessorTest {
    @Mock
    private StorageStrategyRegistry storageStrategyRegistry;
    @Mock
    private StorageOperationRegistry storageOperationRegistry;
    @Mock
    private StorageStrategy storageStrategy;
    @InjectMocks
    private MediaProcessor mediaProcessor;

    @Test
    @DisplayName("Should set image url when storage and operation options are not null")
    void shouldSetImageUrl_whenOperationAndStorageIsNotNull() throws Exception {
        // given
        mediaProcessor.setStorage(StorageType.S3_STORAGE.name());
        mediaProcessor.setOperation(StoreOperation.GET_URL.name());

        TestEntity entity = new TestEntity("1", "test");
        when(storageStrategyRegistry.applyStrategy(StorageType.S3_STORAGE))
                .thenReturn(storageStrategy);

        when(storageOperationRegistry.getStrategy(StoreOperation.GET_URL))
                .thenReturn((storageStrategy, mediaEntity) -> "https://image.com/test.png");;

        // when
        CsvEntity<?> result = mediaProcessor.process(entity);
        // then
        assertSame(entity, result);
        assertEquals("https://image.com/test.png", entity.getImageUrl());

        verify(storageStrategyRegistry)
                .applyStrategy(StorageType.S3_STORAGE);

        verify(storageOperationRegistry)
                .getStrategy(StoreOperation.GET_URL);
    }

    @Test
    @DisplayName("Should return same entity when entity is not media entity")
    void shouldReturnSameEntity_whenEntityIsNotMedia() throws Exception {
        // given
        mediaProcessor.setStorage(StorageType.S3_STORAGE.name());
        mediaProcessor.setOperation(StoreOperation.GET_URL.name());

        LaunchEntity entity = new LaunchEntity();
        // when
        CsvEntity<?> result = mediaProcessor.process(entity);
        // then
        assertThat(result).isSameAs(entity);
        verifyNoInteractions(storageStrategyRegistry);
        verifyNoInteractions(storageOperationRegistry);
    }

    @Test
    @DisplayName("Should throw exception when storage is invalid type")
    void shouldThrowInvalidStoreProviderException_whenStorageInvalidType() {
        mediaProcessor.setStorage("INVALID_STORAGE");
        mediaProcessor.setOperation(StoreOperation.LOCAL_SAVE.name());

        TestEntity entity = new TestEntity("3", "test");

        assertThatThrownBy(() -> mediaProcessor.process(entity))
                .isInstanceOf(InvalidStoreProviderException.class);
    }

    @Test
    @DisplayName("Should throw exception when operation is null or empty")
    void shouldThrowInvalidStoreOperationException_whenStorageOperationInvalidType() {
        mediaProcessor.setStorage(StorageType.S3_STORAGE.name());
        mediaProcessor.setOperation("INVALID_STORAGE");
        TestEntity entity = new TestEntity("3", "test");

        assertThatThrownBy(() -> mediaProcessor.process(entity))
                .isInstanceOf(InvalidStoreOperationException.class);
    }

    @Test
    @DisplayName("Should throw exception when storage is null or empty")
    void shouldThrowInvalidStoreProviderException_whenStorageInvalid() {

        TestEntity entity = new TestEntity("3", "test");

        assertThatThrownBy(() -> mediaProcessor.process(entity))
                .isInstanceOf(InvalidStoreProviderException.class);
    }

    @Test
    @DisplayName("Should throw exception when operation is null or empty")
    void shouldThrowInvalidStoreOperationException_whenStorageOperationInvalid() {
        mediaProcessor.setStorage(StorageType.S3_STORAGE.name());
        TestEntity entity = new TestEntity("3", "test");

        assertThatThrownBy(() -> mediaProcessor.process(entity))
                .isInstanceOf(InvalidStoreOperationException.class);
    }
}