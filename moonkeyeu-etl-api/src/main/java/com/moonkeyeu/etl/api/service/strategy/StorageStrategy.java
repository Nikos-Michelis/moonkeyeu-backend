package com.moonkeyeu.etl.api.service.strategy;

import com.moonkeyeu.etl.api.model.ImageEntity;

import java.io.IOException;
import java.net.MalformedURLException;

public interface StorageStrategy {
    String save(ImageEntity imageEntity) throws IOException;
    String getUrl(ImageEntity imageEntity) throws MalformedURLException;
}
