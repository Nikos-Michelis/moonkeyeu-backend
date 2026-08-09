package com.moonkeyeu.etl.api.configuration.batch.processors;

import com.moonkeyeu.etl.api.dto.storage.StorageType;
import com.moonkeyeu.etl.api.dto.storage.StoreOperation;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.ImageEntity;
import com.moonkeyeu.etl.api.strategy.registry.StorageOperationRegistry;
import com.moonkeyeu.etl.api.strategy.StorageStrategy;
import com.moonkeyeu.etl.api.strategy.registry.StorageStrategyRegistry;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@StepScope
@Setter
@RequiredArgsConstructor
public class MediaProcessor implements ItemProcessor<CsvEntity<?>, CsvEntity<?>> {
    @Value("#{jobParameters['storage'] ?: null}")
    private String storage;
    @Value("#{jobParameters['operation'] ?: null}")
    private String operation;
    private final StorageStrategyRegistry storageStrategyRegistry;
    private final StorageOperationRegistry storageOperationRegistry;


    @Override
    public CsvEntity<?> process(CsvEntity<?> entity) throws IOException {

        if (!(entity instanceof ImageEntity imageEntity)) {
            return entity;
        }

        StorageType storageType = StorageType.from(storage);
        StoreOperation storeOperation = StoreOperation.from(operation);
        StorageStrategy strategy = storageStrategyRegistry.applyStrategy(storageType);
        String imageUrl = storageOperationRegistry.getStrategy(storeOperation).apply(strategy, imageEntity);
        imageEntity.setImageUrl(imageUrl);
        return entity;
    }
}
