package com.moonkeyeu.etl.api.service.impl.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JobExecutionService {
    private final JobLauncher jobLauncher;

    public JobExecutionService(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    public JobExecution jobLauncher(String jobName, JobParameters jobParameters, Job dataProcessingJob) {
        try {
            log.debug("Started new job execution for {}: {}", jobName, jobParameters);
            return jobLauncher.run(dataProcessingJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            throw new RuntimeException(e);
        }
    }
}
