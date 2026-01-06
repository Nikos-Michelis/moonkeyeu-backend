package com.moonkeyeu.etl.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class JobBuilderService {
    private final JobLauncher jobLauncher;
    private final JobOperator jobOperator;

    public JobBuilderService(JobLauncher jobLauncher, JobOperator jobOperator) {
        this.jobLauncher = jobLauncher;
        this.jobOperator = jobOperator;
    }

    public void stopJobByName(String jobName) throws NoSuchJobException {
        Set<Long> executionIds = jobOperator.getRunningExecutions(jobName);
        if (executionIds.isEmpty()) {
            log.debug("No running executions found for job {}", jobName);
            return;
        }
        if (executionIds.size() > 1) executionIds.forEach(this::stopByExecutionId);
    }

    private boolean stopByExecutionId(Long executionId) {
        try {
            return jobOperator.stop(executionId);
        } catch (JobExecutionNotRunningException | NoSuchJobExecutionException e) {
            log.warn("Execution {} was not running", executionId);
            return false;
        }
    }

    public JobExecution jobLauncher(String jobName, JobParameters jobParameters, Job dataProcessingJob) throws NoSuchJobException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        //stopJobByName(jobName);
        JobExecution jobExecution = jobLauncher.run(dataProcessingJob, jobParameters);
        log.debug("Started new job execution for {}: {}", jobName, jobParameters);
        return jobExecution;
    }
}
