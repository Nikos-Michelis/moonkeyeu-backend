package com.moonkeyeu.etl.api.strategy;

import com.moonkeyeu.etl.api.pipeline.ll2.media.StorableImage;

public interface StorageStrategy {
    String save(StorableImage image);
    String getUrl(StorableImage image);
    String getBaseUrl();
}
