package com.moonkeyeu.etl.api.unit.strategies.registry;

import com.moonkeyeu.etl.api.dto.storage.StoreOperation;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreOperationException;
import com.moonkeyeu.etl.api.strategy.registry.StorageOperationRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageOperationRegistryTest {

    @InjectMocks
    private StorageOperationRegistry registry;

    @ParameterizedTest
    @ValueSource(strings = {"LOCAL_SAVE", "S3_UPLOAD", "GET_URL"})
    @DisplayName("Should return non-null strategy for all valid operations")
    void shouldReturnStrategy_WhenStorageOperationStrategyIsValid(String operationName) {
        // Arrange
        StoreOperation operation = StoreOperation.valueOf(operationName);

        // Act
        var strategy = registry.getStrategy(operation);

        // Assert
        assertNotNull(strategy);
    }

    @Test
    @DisplayName("Should throw InvalidStoreOperationException when operation is null")
    void shouldThrowException_WhenOperationIsNull() {
        // Act & Assert
        InvalidStoreOperationException exception = assertThrows(
                InvalidStoreOperationException.class,
                () -> registry.getStrategy(null)
        );

        assertEquals(
                "The store operation could not be completed because the current state is invalid.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Should throw InvalidStoreOperationException when operation is unsupported")
    void shouldThrowException_WhenOperationIsUnsupported() {
        // Arrange
        StoreOperation unsupportedOperation = mock(StoreOperation.class);

        // Act & Assert
        InvalidStoreOperationException exception = assertThrows(
                InvalidStoreOperationException.class,
                () -> registry.getStrategy(unsupportedOperation)
        );

        assertTrue(exception.getMessage().contains("Unsupported store operation"));
    }
}