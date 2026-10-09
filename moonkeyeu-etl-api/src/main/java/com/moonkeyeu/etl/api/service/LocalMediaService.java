package com.moonkeyeu.etl.api.service;

import com.moonkeyeu.etl.api.pipeline.ll2.media.StorableImage;

import java.io.IOException;
import java.net.MalformedURLException;

public interface LocalMediaService {
    String saveMediaLocal(StorableImage item, String localDir) throws IOException;
    String getLocalHostUrl(StorableImage image) throws MalformedURLException;
    String getBaseUrl();
}
