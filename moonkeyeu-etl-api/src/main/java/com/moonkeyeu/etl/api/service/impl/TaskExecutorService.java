package com.moonkeyeu.etl.api.service.impl;

import com.moonkeyeu.etl.api.dto.chunks.JobParamsDTO;
import com.moonkeyeu.etl.api.service.JobBuilderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.flow.FlowExecution;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskExecutorService {
    @Qualifier("runMidnightUpdateJob")
    private final Job runMidnightUpdateJob;
    @Qualifier("runDailyUpdateJob")
    private final Job runDailyUpdateJob;
    @Qualifier("runBulkInsertJob")
    private final Job runBulkInsertJob;
    private final JobBuilderService jobBuilderService;

    //@Scheduled(cron = "0 0/110 1-22 * * *")
    public void dailyJobSequence() {
        try {
            JobParameters jobParameters = JobParamsDTO.builder()
                    .skipCsv(false)
                    .skipJson(false)
                    .skipS3BucketUpload(false)
                    .build()
                    .toJobParameters();

            jobBuilderService.jobLauncher("runDailyUpdateJob", jobParameters, runDailyUpdateJob);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException | NoSuchJobException e) {
            log.error("Unexpected error during dailyJobSequence: {}", e.getMessage(), e);
        }
    }

   // @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 */1 * * * *")
    public void midnightJobSequence() {
        try {

            JobParameters jobParameters = JobParamsDTO.builder()
                    .skipCsv(false)
                    .skipJson(false)
                    .skipS3BucketUpload(true)
                    .build()
                    .toJobParameters();

            jobBuilderService.jobLauncher("runMidnightUpdateJob", jobParameters, runMidnightUpdateJob);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException | NoSuchJobException e) {
            log.error("Unexpected error during dailyJobSequence: {}", e.getMessage(), e);
        }
    }

   // @Scheduled(fixedRate = 10000)
    @Profile("dev")
    public void bulkInsertsJob() throws NoSuchJobException {
        try {
            JobParameters jobParameters = JobParamsDTO.builder()
                    .skipCsv(false)
                    .skipJson(true)
                    .skipS3BucketUpload(true)
                    .build()
                    .toJobParameters();
            jobBuilderService.jobLauncher("runBulkInsertJob", jobParameters, runBulkInsertJob);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException | RuntimeException e) {
            log.error("Unexpected error during bulkInsertsJob: {}", e.getMessage(), e);
        }
    }
}
