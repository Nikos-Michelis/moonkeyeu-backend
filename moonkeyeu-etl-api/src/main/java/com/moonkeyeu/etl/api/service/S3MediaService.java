package com.moonkeyeu.etl.api.service;

import com.moonkeyeu.etl.api.model.ImageEntity;

import java.io.IOException;
import java.net.MalformedURLException;

public interface S3MediaService {
    String saveMediaToS3(ImageEntity item, String bucketName) throws IOException;
    String getCloudFrontUrl(ImageEntity item) throws MalformedURLException;
}
