package com.moonkeyeu.etl.api.service;

public interface S3StorageService {
    void save(byte[] resourceUrl, String s3Key, String bucketName);
    boolean existsByKey(String s3Key, String bucketName);
    void upload(String s3Key, String bucketName, byte[] data);
}
