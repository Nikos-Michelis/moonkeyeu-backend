package com.moonkeyeu.etl.api.service;

import com.moonkeyeu.etl.api.model.ImageEntity;

import java.io.IOException;

public interface LocalMediaService {
    String saveMediaLocal(ImageEntity item, String localDir) throws IOException;
}
