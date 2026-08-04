package com.moonkeyeu.etl.api.service.strategy;

import com.moonkeyeu.etl.api.configuration.files.FilePathProvider;
import com.moonkeyeu.etl.api.configuration.files.RootConfig;
import com.moonkeyeu.etl.api.model.ImageEntity;
import com.moonkeyeu.etl.api.service.LocalMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;

@RequiredArgsConstructor
public class LocalStorageStrategy implements StorageStrategy {
    private final LocalMediaService localMediaService;
    private final RootConfig rootConfig;
    private final FilePathProvider filePathProvider;

    @Override
    public String save(ImageEntity imageEntity) throws IOException {
        return localMediaService.saveMediaLocal(imageEntity, filePathProvider.getImagesDir(rootConfig.getImagesRootFolder()));
    }

    @Override
    public String getUrl(ImageEntity imageEntity) throws MalformedURLException {
        return localMediaService.getLocalHostUrl(imageEntity);
    }
}
