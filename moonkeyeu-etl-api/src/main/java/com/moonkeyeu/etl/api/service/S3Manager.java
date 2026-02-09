package com.moonkeyeu.etl.api.service;

import software.amazon.awssdk.core.sync.RequestBody;

public interface S3Manager {
    void putObject(String bucketName, String key,  RequestBody requestBody);
    boolean isObjectExists(String bucketName, String key);
}
