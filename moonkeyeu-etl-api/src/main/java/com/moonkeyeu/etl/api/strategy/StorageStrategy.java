package com.moonkeyeu.etl.api.strategy;

import com.moonkeyeu.etl.api.model.ImageEntity;

public interface StorageStrategy {
    String save(ImageEntity imageEntity);
    String getUrl(ImageEntity imageEntity);
}
