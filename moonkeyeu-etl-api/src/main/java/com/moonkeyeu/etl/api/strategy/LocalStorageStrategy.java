package com.moonkeyeu.etl.api.strategy;

import com.moonkeyeu.etl.api.configuration.files.FilePathProvider;
import com.moonkeyeu.etl.api.configuration.files.RootConfig;
import com.moonkeyeu.etl.api.model.ImageEntity;
import com.moonkeyeu.etl.api.service.LocalMediaService;
import com.moonkeyeu.etl.api.settings.exceptions.LocalStorageException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.MalformedURLException;

@RequiredArgsConstructor
public class LocalStorageStrategy implements StorageStrategy {
    private final LocalMediaService localMediaService;
    private final RootConfig rootConfig;
    private final FilePathProvider filePathProvider;

    @Override
    public String save(ImageEntity imageEntity) {
        try {
            String directoryPath = filePathProvider.getImagesDir(rootConfig.getImagesRootFolder());
            return localMediaService.saveMediaLocal(imageEntity, directoryPath);
        } catch (IOException e) {
            throw new LocalStorageException("Failed to save media locally: " + e.getMessage());
        }
    }

    @Override
    public String getUrl(ImageEntity imageEntity) {
        try {
            return localMediaService.getLocalHostUrl(imageEntity);
        } catch (MalformedURLException e) {
            throw new LocalStorageException("Failed to extract localhost url:  " + e.getMessage());
        }
    }
}
