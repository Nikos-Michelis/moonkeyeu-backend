package com.moonkeyeu.etl.api.service.job;

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
        if (launchesUpdateJob.getStatus() == BatchStatus.COMPLETED) {
            return BatchStatus.COMPLETED;
        }

        //log.warn("LaunchesUpdateJob did not complete on first run. Status: {}", launchesUpdateJob.getStatus());
        JobExecution updateAgenciesJob = jobExecutionService.jobLauncher("runUpdateAgenciesJob", jobParameters, runUpdateAgenciesJob);
        if (updateAgenciesJob.getStatus() == BatchStatus.FAILED) {
            log.error("UpdateAgenciesJob failed. Aborting sequence.");
            return BatchStatus.FAILED;
        }

        //log.info("UpdateAgenciesJob completed successfully with status: {}", updateAgenciesJob.getStatus());
        JobParameters retryParams = addRetryTimestamp(jobParameters);
        JobExecution retryLaunchesUpdateJob = jobExecutionService.jobLauncher("runDailyUpdateJob", retryParams, runDailyUpdateJob);

        return retryLaunchesUpdateJob.getStatus();
    }

    public BatchStatus midnightJobExecution() {

        JobParameters jobParameters = JobParamsDTO.builder()
                .skipCsv(false)
                .skipJson(false)
                .skipS3BucketUpload(true)
                .build()
                .toJobParameters();

        JobExecution launchesUpdateJob = jobExecutionService.jobLauncher("runLaunchesUpdateJob", jobParameters, runLaunchesUpdateJob);
        if (launchesUpdateJob.getStatus() == BatchStatus.COMPLETED) {
            return BatchStatus.COMPLETED;
        }

        //log.warn("LaunchesUpdateJob did not complete on first run. Status: {}", launchesUpdateJob.getStatus());
        JobExecution updateAgenciesJob = jobExecutionService.jobLauncher("runUpdateAgenciesJob", jobParameters, runUpdateAgenciesJob);
        if (updateAgenciesJob.getStatus() == BatchStatus.FAILED) {
            log.error("UpdateAgenciesJob failed. Aborting sequence.");
            return BatchStatus.FAILED;
        }

        //log.info("UpdateAgenciesJob completed successfully with status: {}", updateAgenciesJob.getStatus());
        JobParameters retryParams = addRetryTimestamp(jobParameters);
        JobExecution retryLaunchesUpdateJob = jobExecutionService.jobLauncher("runLaunchesUpdateJob", retryParams, runLaunchesUpdateJob);

        return retryLaunchesUpdateJob.getStatus();
    }
     public void bulkInsertJobExecution() {
         JobParameters jobParameters = JobParamsDTO.builder()
                 .skipCsv(false)
                 .skipJson(true)
                 .skipS3BucketUpload(true)
                 .build()
                 .toJobParameters();
        jobExecutionService.jobLauncher("runBulkInsertJob", jobParameters, runBulkInsertJob);
    }

    private JobParameters addRetryTimestamp(JobParameters original) {
        return new JobParametersBuilder(original)
                .addLong("retryTimestamp", System.currentTimeMillis())
                .toJobParameters();
    }

}
