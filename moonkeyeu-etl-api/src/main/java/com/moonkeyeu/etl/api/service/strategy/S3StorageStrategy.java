package com.moonkeyeu.etl.api.service.strategy;

import com.moonkeyeu.etl.api.configuration.s3.S3Buckets;
import com.moonkeyeu.etl.api.model.ImageEntity;
import com.moonkeyeu.etl.api.service.S3MediaService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.MalformedURLException;


@RequiredArgsConstructor
public class S3StorageStrategy implements StorageStrategy {
    private final S3MediaService s3MediaService;
    private final S3Buckets s3Buckets;

    @Override
    public String save(ImageEntity imageEntity) throws IOException {
        return s3MediaService.saveMediaToS3(imageEntity, s3Buckets.getBucketName());
    }

    @Override
    public String getUrl(ImageEntity imageEntity) throws MalformedURLException {
        return s3MediaService.getCloudFrontUrl(imageEntity);
    }
}
