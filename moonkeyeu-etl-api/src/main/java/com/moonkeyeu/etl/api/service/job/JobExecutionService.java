package com.moonkeyeu.etl.api.service.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class JobExecutionService {
    private final JobLauncher jobLauncher;
    private final JobOperator jobOperator;

    public JobExecutionService(JobLauncher jobLauncher, JobOperator jobOperator) {
        this.jobLauncher = jobLauncher;
        this.jobOperator = jobOperator;
    }

    public int stopJobByName(String jobName) throws NoSuchJobException {
        Set<Long> executionIds = jobOperator.getRunningExecutions(jobName);

        if (executionIds.isEmpty()) {
            return 0;
        }

        int stoppedCount = 0;

        for (Long executionId : executionIds) {
            if (stopByExecutionId(executionId)) {
                stoppedCount++;
            }
        }

        return stoppedCount;
    }


    private boolean stopByExecutionId(Long executionId) {
        try {
            return jobOperator.stop(executionId);
        } catch (JobExecutionNotRunningException | NoSuchJobExecutionException e) {
            return false;
        }
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
