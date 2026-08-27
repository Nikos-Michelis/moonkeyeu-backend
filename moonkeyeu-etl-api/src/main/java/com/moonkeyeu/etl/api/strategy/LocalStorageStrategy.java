package com.moonkeyeu.etl.api.strategy;

import com.moonkeyeu.etl.api.configuration.files.RootConfig;
import com.moonkeyeu.etl.api.pipeline.ll2.media.StorableImage;
import com.moonkeyeu.etl.api.service.LocalMediaService;
import com.moonkeyeu.etl.api.settings.exceptions.LocalStorageException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.MalformedURLException;

@RequiredArgsConstructor
public class LocalStorageStrategy implements StorageStrategy {

    private final LocalMediaService localMediaService;
    private final RootConfig rootConfig;

    @Override
    public String save(StorableImage image) {
        try {
            return localMediaService.saveMediaLocal(image, rootConfig.getImagesRootFolder());
        } catch (IOException e) {
            throw new LocalStorageException("Failed to save media locally: " + e.getMessage());
        }
    }

    @Override
    public String getUrl(StorableImage image) {
        try {
            return localMediaService.getLocalHostUrl(image);
        } catch (MalformedURLException e) {
            throw new LocalStorageException("Failed to extract localhost url:  " + e.getMessage());
        }
    }

    @Override
    public String getBaseUrl() {
        return localMediaService.getBaseUrl();
    }
}
