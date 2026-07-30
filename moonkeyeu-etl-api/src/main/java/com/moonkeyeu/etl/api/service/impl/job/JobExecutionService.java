package com.moonkeyeu.etl.api.service.impl.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobExecutionService {
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;

    public JobExecutionService(JobLauncher jobLauncher, JobExplorer jobExplorer, JobOperator jobOperator) {
        this.jobLauncher = jobLauncher;
        this.jobExplorer = jobExplorer;
        this.jobOperator = jobOperator;
    }

    public boolean hasAnyRunningJobs() {
        return jobExplorer.getJobNames().stream()
                .flatMap(jobName -> jobExplorer.findRunningJobExecutions(jobName).stream())
                .anyMatch(jobExecution -> jobExecution.getStatus() == BatchStatus.STARTED);
    }

    public void stopAllRunningJobs() {
        List<String> jobNames = jobExplorer.getJobNames();
        for (String jobName : jobNames) {
            Set<JobExecution> runningExecutions = jobExplorer.findRunningJobExecutions(jobName);
            for (JobExecution execution : runningExecutions) {
                stopExecution(execution);
            }
        }
    }

    private void stopExecution(JobExecution execution) {
        try {
            jobOperator.stop(execution.getId());
        } catch (NoSuchJobExecutionException | JobExecutionNotRunningException e) {
            throw new RuntimeException(e);
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
