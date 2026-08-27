package com.moonkeyeu.etl.api.strategy;

import com.moonkeyeu.etl.api.configuration.s3.S3Buckets;
import com.moonkeyeu.etl.api.pipeline.ll2.media.StorableImage;
import com.moonkeyeu.etl.api.service.S3MediaService;
import com.moonkeyeu.etl.api.settings.exceptions.S3StorageException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.MalformedURLException;

@RequiredArgsConstructor
public class S3StorageStrategy implements StorageStrategy {
    private final S3MediaService s3MediaService;
    private final S3Buckets s3Buckets;

    @Override
    public String save(StorableImage image) {
        try {
            return s3MediaService.saveMediaToS3(image, s3Buckets.getBucketName());
        } catch (IOException e) {
            throw new S3StorageException("Failed to upload media to Amazon S3: " + e.getMessage());
        }
    }

    @Override
    public String getUrl(StorableImage image) {
        try {
            return s3MediaService.getCloudFrontUrl(image);
        } catch (MalformedURLException e) {
            throw new S3StorageException("Failed to extract cloudFront url: " + e.getMessage());
        }
    }

    @Override
    public String getBaseUrl() {
        return s3MediaService.getBaseUrl();
    }
}
