package com.moonkeyeu.etl.api.service.impl.s3;

import com.moonkeyeu.etl.api.service.S3CrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Service
@Slf4j
public class S3CrudServiceImpl implements S3CrudService {

    private final S3Client s3Client;

    public S3CrudServiceImpl(S3Client s3Client){
        this.s3Client = s3Client;
    }

    @Override
    public void putObject(String bucketName, String key,  RequestBody requestBody) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.putObject(
                putObjectRequest, requestBody);

    }

    @Override
    public void deleteObject(String bucketName, String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    @Override
    public boolean isObjectExists(String bucketName, String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            log.error("Error checking object existence: {}", e.awsErrorDetails().errorMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return false;
        }
    }
}
