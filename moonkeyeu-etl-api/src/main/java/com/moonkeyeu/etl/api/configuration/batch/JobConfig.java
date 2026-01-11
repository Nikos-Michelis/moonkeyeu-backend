package com.moonkeyeu.etl.api.configuration.batch;

import com.moonkeyeu.etl.api.configuration.batch.listeners.JobCompletionListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class JobConfig {
    private final JobRepository jobRepository;
    private final Flow agenciesFlow;
    private final Flow allLatestLaunchesFlow;
    private final Flow latestLaunchesUntilFlow;
    private final Flow bulkInsertFlow;
    private final JobCompletionListener jobCompletionListener;
    @Bean
    public Job runDailyUpdateJob() {
        return new JobBuilder("runDailyUpdateJob", jobRepository)
                .start(latestLaunchesUntilFlow).end().listener(jobCompletionListener).build();
    }

    @Bean
    public Job runLaunchesUpdateJob() {
        return new JobBuilder("runLaunchesUpdateJob", jobRepository)
                .start(allLatestLaunchesFlow).end().listener(jobCompletionListener).build();
    }

    @Bean
    public Job runUpdateAgenciesJob() {
        return new JobBuilder("runUpdateAgenciesJob", jobRepository)
                .start(agenciesFlow).end().listener(jobCompletionListener).build();
    }

    @Bean
    public Job runBulkInsertJob() {
        return new JobBuilder("runBulkInsertJob", jobRepository)
                .start(bulkInsertFlow).end().listener(jobCompletionListener).build();
    }
}
