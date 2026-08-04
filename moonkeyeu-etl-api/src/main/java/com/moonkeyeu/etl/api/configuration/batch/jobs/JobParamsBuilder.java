package com.moonkeyeu.etl.api.configuration.batch.jobs;

import com.moonkeyeu.etl.api.dto.storage.CleanupType;
import com.moonkeyeu.etl.api.dto.storage.StorageType;
import com.moonkeyeu.etl.api.dto.storage.StoreOperation;
import lombok.Builder;
import lombok.Data;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Data
@Builder
public class JobParamsBuilder {
    private String storage;
    private StoreOperation operation;
    private CleanupType cleanup;

    public JobParameters toJobParameters() {
        return new JobParametersBuilder()
                .addString("storage", storage)
                .addString("operation", operation.name())
                .addString("cleanup", cleanup.name())
                .addLocalDateTime("start_at", LocalDateTime.now().atOffset(ZoneOffset.UTC).toLocalDateTime())
                .addString("uniqueId", UUID.randomUUID().toString())
                .toJobParameters();
    }

    public static JobParameters addRetryTimestamp(JobParameters original) {
        return new JobParametersBuilder(original)
                .addLocalDateTime("retry_at",LocalDateTime.now().atOffset(ZoneOffset.UTC).toLocalDateTime())
                .toJobParameters();
    }
}
