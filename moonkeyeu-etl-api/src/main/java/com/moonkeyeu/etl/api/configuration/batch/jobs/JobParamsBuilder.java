package com.moonkeyeu.etl.api.configuration.batch.jobs;

import com.moonkeyeu.etl.api.dto.storage.CleanupType;
import com.moonkeyeu.etl.api.dto.storage.StorageType;
import com.moonkeyeu.etl.api.dto.storage.StoreOperation;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@RequiredArgsConstructor
public class JobParamsBuilder {
    private StorageType storage;
    private StoreOperation operation;
    private CleanupType cleanup;

    public JobParameters toJobParameters() {
        return new JobParametersBuilder()
                .addString("storage", storage.name())
                .addString("operation", operation.name())
                .addString("cleanup", cleanup.name())
                .addString("startTimestamp", Instant.now().toString())
                .addString("uniqueId", UUID.randomUUID().toString())
                .toJobParameters();
    }

    public static JobParameters addRetryTimestamp(JobParameters original) {
        return new JobParametersBuilder(original)
                .addString("retryTimestamp", Instant.now().toString())
                .toJobParameters();
    }
}
