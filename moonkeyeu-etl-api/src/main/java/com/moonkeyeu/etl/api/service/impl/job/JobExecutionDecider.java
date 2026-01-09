package com.moonkeyeu.etl.api.service.impl.job;

import com.moonkeyeu.etl.api.dto.chunks.JobParamsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobExecutionDecider {
    @Qualifier("runLaunchesUpdateJob")
    private final Job runLaunchesUpdateJob;
    @Qualifier("runDailyUpdateJob")
    private final Job runDailyUpdateJob;
    @Qualifier("runBulkInsertJob")
    private final Job runBulkInsertJob;
    @Qualifier("runBulkInsertJob2")
    private final Job runBulkInsertJob2;
    @Qualifier("runUpdateAgenciesJob")
    private final Job runUpdateAgenciesJob;
    private final JobExecutionService jobExecutionService;

    public BatchStatus dailyJobExecution() {

        JobParameters jobParameters = JobParamsDTO.builder()
                .skipCsv(false)
                .skipJson(false)
                .skipS3BucketUpload(false)
                .build()
                .toJobParameters();

        JobExecution launchesUpdateJob = jobExecutionService.jobLauncher("runDailyUpdateJob", jobParameters, runDailyUpdateJob);
        if (launchesUpdateJob.getStatus() != BatchStatus.COMPLETED) {
            return BatchStatus.FAILED;
        }

        JobExecution updateAgenciesJob = jobExecutionService.jobLauncher("runUpdateAgenciesJob", jobParameters, runUpdateAgenciesJob);
        if (updateAgenciesJob.getStatus() != BatchStatus.COMPLETED) {
            return BatchStatus.FAILED;
        }

        JobParameters retryParams = addRetryTimestamp(jobParameters);
        JobExecution retryLaunchesUpdateJob = jobExecutionService.jobLauncher("runDailyUpdateJob", retryParams, runDailyUpdateJob);
        if (retryLaunchesUpdateJob.getStatus() != BatchStatus.COMPLETED) {
            return BatchStatus.FAILED;
        }
        return BatchStatus.COMPLETED;
    }

    public BatchStatus midnightJobExecution() {

        JobParameters jobParameters = JobParamsDTO.builder()
                .skipCsv(false)
                .skipJson(false)
                .skipS3BucketUpload(true)
                .build()
                .toJobParameters();

        JobExecution launchesUpdateJob = jobExecutionService.jobLauncher("runLaunchesUpdateJob", jobParameters, runLaunchesUpdateJob);
        if (launchesUpdateJob.getStatus() != BatchStatus.COMPLETED) {
            return BatchStatus.FAILED;
        }

        JobExecution updateAgenciesJob = jobExecutionService.jobLauncher("runUpdateAgenciesJob", jobParameters, runUpdateAgenciesJob);
        if (updateAgenciesJob.getStatus() != BatchStatus.COMPLETED) {
            log.error("UpdateAgenciesJob failed. Aborting sequence.");
            return BatchStatus.FAILED;
        }

        JobParameters retryParams = addRetryTimestamp(jobParameters);
        JobExecution retryLaunchesUpdateJob = jobExecutionService.jobLauncher("runLaunchesUpdateJob", retryParams, runLaunchesUpdateJob);
        if (retryLaunchesUpdateJob.getStatus() != BatchStatus.COMPLETED) {
            return BatchStatus.FAILED;
        }
        return BatchStatus.COMPLETED;
    }
     public BatchStatus bulkInsertJobExecution() {
         JobParameters jobParameters = JobParamsDTO.builder()
                 .skipCsv(false)
                 .skipJson(true)
                 .skipS3BucketUpload(true)
                 .build()
                 .toJobParameters();
         JobExecution bulkInsertJob = jobExecutionService.jobLauncher("runBulkInsertJob", jobParameters, runBulkInsertJob);
         if (bulkInsertJob.getStatus() != BatchStatus.COMPLETED) {
             return BatchStatus.FAILED;
         }
         return BatchStatus.COMPLETED;
     }

    private JobParameters addRetryTimestamp(JobParameters original) {
        return new JobParametersBuilder(original)
                .addLong("retryTimestamp", System.currentTimeMillis())
                .toJobParameters();
    }

}
