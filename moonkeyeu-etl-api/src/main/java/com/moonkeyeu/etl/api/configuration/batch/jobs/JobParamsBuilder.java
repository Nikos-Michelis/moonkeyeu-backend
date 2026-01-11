package com.moonkeyeu.etl.api.configuration.batch.jobs;

import com.moonkeyeu.etl.api.dto.storage.CleanupType;
import com.moonkeyeu.etl.api.dto.storage.StorageType;
import com.moonkeyeu.etl.api.dto.storage.StoreOperation;
import lombok.Builder;
import lombok.Data;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.time.Instant;
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
                .addString("timestamp", Instant.now().toString())
                .addString("uniqueId", UUID.randomUUID().toString())
                .toJobParameters();
    }
}
