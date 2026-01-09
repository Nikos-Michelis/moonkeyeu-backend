package com.moonkeyeu.etl.api.configuration.batch.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobCompletionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job: {} started...", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Job: {} completed successfully", jobExecution.getJobInstance().getJobName());
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("Job: {} failed", jobExecution.getJobInstance().getJobName());
            jobExecution.getAllFailureExceptions().forEach(ex -> log.error("Failure reason", ex));
        }
    }
}