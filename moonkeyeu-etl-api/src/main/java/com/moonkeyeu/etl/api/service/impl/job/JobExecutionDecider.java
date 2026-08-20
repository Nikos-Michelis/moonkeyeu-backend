package com.moonkeyeu.etl.api.service.impl.job;

import com.moonkeyeu.etl.api.configuration.batch.jobs.JobParamsBuilder;
import com.moonkeyeu.etl.api.configuration.batch.jobs.CleanupType;
import com.moonkeyeu.etl.api.configuration.batch.jobs.StorageType;
import com.moonkeyeu.etl.api.configuration.batch.jobs.StoreOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobExecutionDecider {
    @Qualifier("runUpdateAllLaunchesJob")
    private final Job runUpdateAllLaunchesJob;
    @Qualifier("runUpdateDailyLaunchesJob")
    private final Job runUpdateDailyLaunchesJob;
    @Qualifier("runBulkInsertJob")
    private final Job runBulkInsertJob;
    @Qualifier("runUpdateAgenciesJob")
    private final Job runUpdateAgenciesJob;
    private final JobExecutionService jobExecutionService;

    public BatchStatus dailyJobExecution() {
        JobParameters jobParameters = JobParamsBuilder.builder()
                .storage(StorageType.S3_STORAGE)
                .operation(StoreOperation.GET_URL)
                .cleanup(CleanupType.ALL)
                .build()
                .toJobParameters();

        if (jobExecutionService.hasAnyRunningJobs()) jobExecutionService.stopAllRunningJobs();

        JobExecution launchesUpdateJob = jobExecutionService.jobLauncher("runUpdateDailyLaunchesJob", jobParameters, runUpdateDailyLaunchesJob);
        if (launchesUpdateJob.getStatus() == BatchStatus.COMPLETED) return BatchStatus.COMPLETED;
        log.warn("dailyJobExecution: Launches fetching failed {} try to syncing agencies", BatchStatus.FAILED);

        JobExecution updateAgenciesJob = jobExecutionService.jobLauncher("runUpdateAgenciesJob", jobParameters, runUpdateAgenciesJob);
        if (updateAgenciesJob.getStatus() != BatchStatus.COMPLETED) {
            log.error("dailyJobExecution: Agencies fetching failed {} Abort the job execution", BatchStatus.FAILED);
            return BatchStatus.FAILED;
        }

        JobParameters retryParams = JobParamsBuilder.addRetryTimestamp(jobParameters);
        JobExecution retryLaunchesUpdateJob = jobExecutionService.jobLauncher("runUpdateDailyLaunchesJob", retryParams, runUpdateDailyLaunchesJob);
        if (retryLaunchesUpdateJob.getStatus() != BatchStatus.COMPLETED) {
            log.error("dailyJobExecution: Launches fetching failed {} Abort the job execution", BatchStatus.FAILED);
            return BatchStatus.FAILED;
        }
        return BatchStatus.COMPLETED;
    }

    public BatchStatus midnightJobExecution() {
        JobParameters jobParameters = JobParamsBuilder.builder()
                .storage(StorageType.S3_STORAGE)
                .operation(StoreOperation.S3_UPLOAD)
                .cleanup(CleanupType.ALL)
                .build()
                .toJobParameters();

        if (jobExecutionService.hasAnyRunningJobs()) jobExecutionService.stopAllRunningJobs();

        JobExecution launchesUpdateJob = jobExecutionService.jobLauncher("runUpdateAllLaunchesJob", jobParameters, runUpdateAllLaunchesJob);
        if (launchesUpdateJob.getStatus() == BatchStatus.COMPLETED) return BatchStatus.COMPLETED;
        log.warn("midnightJobExecution: Launches fetching failed {} try to syncing agencies", BatchStatus.FAILED);

        JobExecution updateAgenciesJob = jobExecutionService.jobLauncher("runUpdateAgenciesJob", jobParameters, runUpdateAgenciesJob);
        if (updateAgenciesJob.getStatus() != BatchStatus.COMPLETED) {
            log.error("midnightJobExecution: Agencies fetching failed {} Abort the job execution", BatchStatus.FAILED);
            return BatchStatus.FAILED;
        }

        JobParameters retryParams = JobParamsBuilder.addRetryTimestamp(jobParameters);
        JobExecution retryLaunchesUpdateJob = jobExecutionService.jobLauncher("runUpdateAllLaunchesJob", retryParams, runUpdateAllLaunchesJob);
        if (retryLaunchesUpdateJob.getStatus() != BatchStatus.COMPLETED) {
            log.error("midnightJobExecution: Launches fetching failed {} Abort the job execution", BatchStatus.FAILED);
            return BatchStatus.FAILED;
        }
        return BatchStatus.COMPLETED;
    }

     public BatchStatus bulkInsertJobExecution() {
         JobParameters jobParameters = JobParamsBuilder.builder()
                 .storage(StorageType.S3_STORAGE)
                 .operation(StoreOperation.GET_URL)
                 .cleanup(CleanupType.NONE)
                 .build()
                 .toJobParameters();

         if (jobExecutionService.hasAnyRunningJobs()) jobExecutionService.stopAllRunningJobs();

         JobExecution bulkInsertJob = jobExecutionService.jobLauncher("runBulkInsertJob", jobParameters, runBulkInsertJob);
         if (bulkInsertJob.getStatus() != BatchStatus.COMPLETED) {
             return BatchStatus.FAILED;
         }
         return BatchStatus.COMPLETED;
     }

}
