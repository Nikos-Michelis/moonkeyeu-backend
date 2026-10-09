package com.moonkeyeu.etl.api.strategy.registry;

import com.moonkeyeu.etl.api.configuration.batch.jobs.StoreOperation;
import com.moonkeyeu.etl.api.pipeline.ll2.media.StorableImage;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreOperationException;
import com.moonkeyeu.etl.api.strategy.StorageStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiFunction;

@Component
public class StorageOperationRegistry {

    private final Map<StoreOperation, BiFunction<StorageStrategy, StorableImage, String>> OPERATIONS = Map.of(
                    StoreOperation.LOCAL_SAVE, StorageStrategy::save,
                    StoreOperation.S3_UPLOAD, StorageStrategy::save,
                    StoreOperation.GET_URL, StorageStrategy::getUrl
            );

    public BiFunction<StorageStrategy, StorableImage, String> getStrategy(StoreOperation operation) {

        if (operation == null) {
            throw new InvalidStoreOperationException("The store operation could not be completed because the current state is invalid.");
        }

        BiFunction<StorageStrategy, StorableImage, String> storageOperation = OPERATIONS.get(operation);

        if (storageOperation == null) {
            throw new InvalidStoreOperationException("Unsupported store operation: " + operation);
        }

        return storageOperation;
    }
}