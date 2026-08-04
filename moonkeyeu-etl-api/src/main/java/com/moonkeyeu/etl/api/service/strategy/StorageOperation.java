package com.moonkeyeu.etl.api.service.strategy;

import com.moonkeyeu.etl.api.model.ImageEntity;

import java.io.IOException;

@FunctionalInterface
public interface StorageOperation {

    String execute(StorageStrategy strategy, ImageEntity entity) throws IOException;

    static StorageOperation save() {
        return StorageStrategy::save;
    }

    static StorageOperation getUrl() {
        return StorageStrategy::getUrl;
    }
}