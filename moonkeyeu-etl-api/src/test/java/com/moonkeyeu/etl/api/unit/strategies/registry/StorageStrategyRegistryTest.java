package com.moonkeyeu.etl.api.unit.strategies.registry;

import com.moonkeyeu.etl.api.configuration.batch.jobs.StorageType;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreProviderException;
import com.moonkeyeu.etl.api.strategy.StorageStrategy;
import com.moonkeyeu.etl.api.strategy.registry.StorageStrategyRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageStrategyRegistryTest {

    private StorageStrategyRegistry registry;

    @Mock
    private StorageStrategy localStrategy;

    @Mock
    private StorageStrategy s3Strategy;

    @Mock
    private StorageStrategy cloudStrategy;

    @Mock
    private StorageType storageType;

    @BeforeEach
    void setUp() {
        Map<String, StorageStrategy> strategies = new HashMap<>();
        strategies.put("LOCAL", localStrategy);
        strategies.put("S3", s3Strategy);

        registry = new StorageStrategyRegistry(strategies);
    }

    @ParameterizedTest
    @CsvSource({
            "LOCAL, localStrategy",
            "S3, s3Strategy",
    })
    @DisplayName("Should return correct strategy for valid storage types")
    void shouldReturnStrategy_whenStorageTypeIsValid(String storageTypeValue, String strategyName) {
        // Arrange
        when(storageType.getType()).thenReturn(storageTypeValue);

        // Act
        StorageStrategy result = registry.applyStrategy(storageType);

        // Assert
        assertNotNull(result);
        verify(storageType).getType();
    }

    @ParameterizedTest
    @ValueSource(strings = { "LOCAL", "S3"})
    @DisplayName("Should return specific strategy instance for each type")
    void shouldReturnSpecificStrategyInstance_whenStorageTypeIsProvided(String storageTypeValue) {
        // Arrange
        when(storageType.getType()).thenReturn(storageTypeValue);

        // Act
        StorageStrategy result = registry.applyStrategy(storageType);

        // Assert
        assertNotNull(result);
        switch (storageTypeValue) {
            case "LOCAL" -> assertEquals(localStrategy, result);
            case "S3" -> assertEquals(s3Strategy, result);
            case "CLOUD" -> assertEquals(cloudStrategy, result);
        }
    }

    @Test
    @DisplayName("Should throw InvalidStoreProviderException when storage type is null")
    void shouldThrowInvalidStoreProviderException_whenStorageTypeIsNull() {
        // Act & Assert
        InvalidStoreProviderException exception = assertThrows(
                InvalidStoreProviderException.class,
                () -> registry.applyStrategy(null)
        );

        assertEquals(
                "Storage could not be completed because the current state is invalid.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Should throw InvalidStoreProviderException when storage type is unsupported")
    void shouldThrowInvalidStoreProviderException_whenStorageTypeIsUnsupported() {
        // Arrange
        when(storageType.getType()).thenReturn("UNSUPPORTED");

        // Act & Assert
        InvalidStoreProviderException exception = assertThrows(
                InvalidStoreProviderException.class,
                () -> registry.applyStrategy(storageType)
        );

        assertTrue(exception.getMessage().contains("Unsupported storage type"));
    }

    @Test
    @DisplayName("Should call getType method on storage type")
    void shouldCallGetType_whenApplyStrategyIsCalled() {
        // Arrange
        when(storageType.getType()).thenReturn("LOCAL");

        // Act
        registry.applyStrategy(storageType);

        // Assert
        verify(storageType).getType();
    }
}