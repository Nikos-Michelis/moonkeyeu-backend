package com.moonkeyeu.etl.api.configuration.batch.jobs;

import lombok.Builder;
import lombok.Data;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Data
@Builder
public class JobParamsBuilder {
    private StorageType storage;
    private StoreOperation operation;
    private CleanupType cleanup;

    public JobParameters toJobParameters() {
        return new JobParametersBuilder()
                .addString("storage", storage.name())
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
