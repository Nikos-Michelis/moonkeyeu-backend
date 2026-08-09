package com.moonkeyeu.etl.api.strategy.registry;

import com.moonkeyeu.etl.api.dto.storage.StorageType;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreProviderException;
import com.moonkeyeu.etl.api.strategy.StorageStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class StorageStrategyRegistry {
    private final Map<String, StorageStrategy> strategies;

    public StorageStrategy applyStrategy(StorageType storageType) throws InvalidStoreProviderException {

        if (storageType == null) {
            throw new InvalidStoreProviderException("Storage could not be completed because the current state is invalid.");
        }

        StorageStrategy strategy = strategies.get(storageType.getType());
        if (strategy == null) {
            throw new InvalidStoreProviderException("Unsupported storage type: " + storageType);
        }

        return strategy;
    }
}
