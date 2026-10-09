package com.moonkeyeu.etl.api.service.impl.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class JobExecutionService {
    private final JobOperator jobOperator;
    private final JobRepository jobRepository;


    public JobExecutionService(JobOperator jobOperator, JobRepository jobRepository) {
        this.jobOperator = jobOperator;
        this.jobRepository = jobRepository;
    }

    public boolean hasAnyRunningJobs() {
        return jobRepository.getJobNames().stream()
                .flatMap(jobName -> jobRepository.findRunningJobExecutions(jobName).stream())
                .anyMatch(jobExecution -> jobExecution.getStatus() == BatchStatus.STARTED);
    }

    public void stopAllRunningJobs() {
        List<String> jobNames = jobRepository.getJobNames();
        for (String jobName : jobNames) {
            Set<JobExecution> runningExecutions = jobRepository.findRunningJobExecutions(jobName);
            for (JobExecution execution : runningExecutions) {
                stopExecution(execution);
            }
        }
    }

    private void stopExecution(JobExecution execution) {
        try {
            jobOperator.stop(execution);
        } catch (JobExecutionNotRunningException e) {
            throw new RuntimeException(e);
        }
    }

    public JobExecution jobLauncher(String jobName, JobParameters jobParameters, Job dataProcessingJob) {
        try {
            log.debug("Started new job execution for {}: {}", jobName, jobParameters);
            return jobOperator.start(dataProcessingJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 InvalidJobParametersException e) {
            throw new RuntimeException(e);
        }
    }
}
