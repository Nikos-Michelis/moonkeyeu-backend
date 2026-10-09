package com.moonkeyeu.etl.api.service;

import com.moonkeyeu.etl.api.pipeline.ll2.media.StorableImage;

import java.io.IOException;
import java.net.MalformedURLException;

public interface S3MediaService {
    String saveMediaToS3(StorableImage item, String bucketName) throws IOException;
    String getCloudFrontUrl(StorableImage item) throws MalformedURLException;
    String getBaseUrl();
}
