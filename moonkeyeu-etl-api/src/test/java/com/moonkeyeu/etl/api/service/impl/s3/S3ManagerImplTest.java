package com.moonkeyeu.etl.api.service.impl.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ManagerImplTest {
    @Mock
    private S3Client s3Client;
    @InjectMocks
    private S3ManagerImpl s3Manager;

    @Test
    @DisplayName("Should upload object to S3 with correct bucket and key")
    void putObject_shouldCallS3ClientWithCorrectArguments() {

        String bucketName = "test-bucket";
        String key = "images/test.jpg";

        RequestBody requestBody = RequestBody.fromString("test-content");

        s3Manager.putObject(bucketName, key, requestBody);

        ArgumentCaptor<PutObjectRequest> requestCaptor =
                ArgumentCaptor.forClass(PutObjectRequest.class);

        verify(s3Client).putObject(requestCaptor.capture(), eq(requestBody));

        PutObjectRequest capturedRequest = requestCaptor.getValue();

        assertThat(capturedRequest.bucket()).isEqualTo(bucketName);
        assertThat(capturedRequest.key()).isEqualTo(key);
    }

    @Test
    @DisplayName("Should return true when object exists in S3")
    void isObjectExists_shouldReturnTrue_whenObjectExists() {

        String bucketName = "test-bucket";
        String key = "file.json";

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(null);

        boolean result = s3Manager.isObjectExists(bucketName, key);

        assertThat(result).isTrue();

        verify(s3Client).headObject(any(HeadObjectRequest.class));
    }

    @Test
    @DisplayName("Should return false when S3 object does not exist")
    void isObjectExists_shouldReturnFalse_whenNoSuchKeyExceptionOccurs() {

        String bucketName = "test-bucket";
        String key = "missing-file.json";

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("Not found").build());

        boolean result = s3Manager.isObjectExists(bucketName, key);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when S3 exception occurs")
    void isObjectExists_shouldReturnFalse_whenS3ExceptionOccurs() {

        String bucketName = "test-bucket";
        String key = "file.json";

        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder()
                .errorMessage("S3 error")
                .build();

        S3Exception s3Exception = (S3Exception) S3Exception.builder()
                .awsErrorDetails(awsErrorDetails)
                .build();

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(s3Exception);

        boolean result = s3Manager.isObjectExists(bucketName, key);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when unexpected exception occurs")
    void isObjectExists_shouldReturnFalse_whenUnexpectedExceptionOccurs() {

        String bucketName = "test-bucket";
        String key = "file.json";

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        boolean result = s3Manager.isObjectExists(bucketName, key);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should call headObject with correct bucket and key")
    void isObjectExists_shouldCallHeadObjectWithCorrectRequest() {

        String bucketName = "bucket";
        String key = "folder/file.txt";

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(null);

        s3Manager.isObjectExists(bucketName, key);

        ArgumentCaptor<HeadObjectRequest> requestCaptor =
                ArgumentCaptor.forClass(HeadObjectRequest.class);

        verify(s3Client).headObject(requestCaptor.capture());

        HeadObjectRequest capturedRequest = requestCaptor.getValue();

        assertThat(capturedRequest.bucket()).isEqualTo(bucketName);
        assertThat(capturedRequest.key()).isEqualTo(key);
    }
}