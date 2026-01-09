package com.moonkeyeu.etl.api.service;

import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.ImageEntity;

public interface S3StorageService {
    void saveMediaToS3(CsvEntity<?> item, String bucketName, boolean skipUpload);
    void setImageUrl(ImageEntity entity, String bucketName, boolean skipUpload);
}
