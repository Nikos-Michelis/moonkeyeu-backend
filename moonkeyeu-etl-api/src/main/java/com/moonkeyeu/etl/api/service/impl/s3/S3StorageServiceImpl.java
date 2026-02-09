package com.moonkeyeu.etl.api.service.impl.s3;

import com.moonkeyeu.etl.api.service.S3StorageService;
import com.moonkeyeu.etl.api.service.S3Manager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;

@Slf4j
@Service
public class S3StorageServiceImpl implements S3StorageService {
     private final S3Manager s3Manager;
    private final CacheManager cacheManager;


    @Autowired
    public S3StorageServiceImpl(S3Manager s3Manager, CacheManager cacheManager) {
        this.s3Manager = s3Manager;
        this.cacheManager = cacheManager;
    }

    @Override
    public void save(byte[] data, String s3Key, String bucketName) {
        upload(s3Key, bucketName, data);
        Cache cache = cacheManager.getCache("processedImages");
        if (cache != null) {
            cache.put(s3Key, true);
        }
    }

    @Override
    public boolean existsByKey(String s3Key, String bucketName) {
        Cache cache = cacheManager.getCache("processedImages");
        if (cache != null && Boolean.TRUE.equals(cache.get(s3Key, Boolean.class))) {
            return true;
        }

        boolean existsInS3 = s3Manager.isObjectExists(bucketName, s3Key);
        if (existsInS3 && cache != null) {
            cache.put(s3Key, true);
        }

        return existsInS3;
    }

    @Override
    public void upload(String s3Key, String bucketName, byte[] data) {
        try {
            RequestBody requestBody = RequestBody.fromBytes(data);
            s3Manager.putObject(bucketName, s3Key, requestBody);
            log.info("Uploaded {} to S3 bucket {}", s3Key, bucketName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload " + s3Key + " to S3", e);
        }
    }
}
