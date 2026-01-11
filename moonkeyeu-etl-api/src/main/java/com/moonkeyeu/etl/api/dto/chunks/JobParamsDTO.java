package com.moonkeyeu.etl.api.dto.chunks;

import lombok.Builder;
import lombok.Data;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class JobParamsDTO {
    private boolean skipCsv;
    private boolean skipJson;
    private boolean s3StorageEnabled;
    private boolean localStorageEnabled;

    public JobParameters toJobParameters() {
        return new JobParametersBuilder()
                .addString("skipCsv", Boolean.toString(skipCsv))
                .addString("skipJson", Boolean.toString(skipJson))
                .addString("s3StorageEnabled", Boolean.toString(s3StorageEnabled))
                .addString("localStorageEnabled", Boolean.toString(localStorageEnabled))
                .addString("timestamp", Instant.now().toString())
                .addString("uniqueId", UUID.randomUUID().toString())
                .toJobParameters();
    }
}
