package com.moonkeyeu.etl.api.service;

import software.amazon.awssdk.core.sync.RequestBody;

public interface S3CrudService {
    void putObject(String bucketName, String key,  RequestBody requestBody);
    void deleteObject(String bucketName, String key);
    boolean isObjectExists(String bucketName, String key);
}
