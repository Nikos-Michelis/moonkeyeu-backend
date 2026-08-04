package com.moonkeyeu.etl.api.configuration.batch.processors;

import com.moonkeyeu.etl.api.dto.storage.StoreOperation;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.ImageEntity;
import com.moonkeyeu.etl.api.service.strategy.StorageOperationRegistry;
import com.moonkeyeu.etl.api.service.strategy.StorageStrategy;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
@StepScope
public class MediaProcessor implements ItemProcessor<CsvEntity<?>, CsvEntity<?>> {
    @Value("#{jobParameters['storage'] ?: null}")
    private String storage;
    @Value("#{jobParameters['operation'] ?: null}")
    private String operation;
    private final Map<String, StorageStrategy> strategies;
    private final StorageOperationRegistry storageOperationRegistry;


    @Override
    public CsvEntity<?> process(CsvEntity<?> entity) throws IOException {

        if (!(entity instanceof ImageEntity imageEntity)) {
            return entity;
        }

        StoreOperation storeOperation = StoreOperation.from(operation);
        StorageStrategy strategy = applyStorageStrategy(storage);
        String imageUrl = storageOperationRegistry.getRegistry(storeOperation).execute(strategy, imageEntity);
        imageEntity.setImageUrl(imageUrl);
        return entity;
    }

    public StorageStrategy applyStorageStrategy(String storageType) {

        if (storage == null || storage.isEmpty()) {
            throw new InvalidStoreProviderException("Storage could not be completed because the current state is invalid.");
        }

        StorageStrategy strategy = strategies.get(storageType);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported storage type: " + storageType);
        }
        return strategy;
    }
}
