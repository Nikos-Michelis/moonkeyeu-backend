package com.moonkeyeu.etl.api.service;

import com.moonkeyeu.etl.api.model.ImageEntity;

import java.io.IOException;

public interface S3MediaService {
    String saveMediaToS3(ImageEntity item, String bucketName, boolean skipUpload) throws IOException;
}
