package com.moonkeyeu.etl.api.service.strategy;

import com.moonkeyeu.etl.api.dto.storage.StoreOperation;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreOperationException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public final class StorageOperationRegistry {

    private final Map<StoreOperation, StorageOperation> OPERATIONS = Map.of(
            StoreOperation.LOCAL_SAVE, StorageOperation.save(),
            StoreOperation.S3_UPLOAD, StorageOperation.save(),
            StoreOperation.GET_URL, StorageOperation.getUrl()
    );

    public StorageOperation getRegistry(StoreOperation operation) {

        if (operation == null) {
            throw new InvalidStoreOperationException("The store operation could not be completed because the current state is invalid.");
        }

        StorageOperation storageOperation = OPERATIONS.get(operation);

        if (storageOperation == null) {
            throw new IllegalArgumentException("Unsupported store operation: " + operation);
        }

        return storageOperation;
    }
}