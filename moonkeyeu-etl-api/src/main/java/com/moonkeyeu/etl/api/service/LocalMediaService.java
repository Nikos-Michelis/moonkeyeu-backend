package com.moonkeyeu.etl.api.service;

import com.moonkeyeu.etl.api.model.ImageEntity;

import java.io.IOException;
import java.net.MalformedURLException;

public interface LocalMediaService {
    String saveMediaLocal(ImageEntity item, String localDir) throws IOException;
    String getLocalHostUrl(ImageEntity imageEntity) throws MalformedURLException;
}
