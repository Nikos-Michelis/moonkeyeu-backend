package com.moonkeyeu.etl.api.configuration.batch.jobs;

import com.moonkeyeu.etl.api.configuration.batch.listeners.JobCompletionListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfig {
    private final JobRepository jobRepository;
    private final Flow syncAllAgenciesFlow;
    private final Flow syncAllLaunchesFlow;
    private final Flow syncYealyLaunchesFlow;
    private final Flow bulkInsertFlow;
    private final JobCompletionListener jobCompletionListener;
    @Bean
    public Job runUpdateDailyLaunchesJob() {
        return new JobBuilder("job-update-daily-launches", jobRepository)
                .start(syncYealyLaunchesFlow).end().listener(jobCompletionListener).build();
    }

    @Bean
    public Job runUpdateAllLaunchesJob() {
        return new JobBuilder("job-update-all-launches", jobRepository)
                .start(syncAllLaunchesFlow).end().listener(jobCompletionListener).build();
    }

    @Bean
    public Job runUpdateAgenciesJob() {
        return new JobBuilder("job-update-agencies", jobRepository)
                .start(syncAllAgenciesFlow).end().listener(jobCompletionListener).build();
    }

    @Bean
    public Job runBulkInsertJob() {
        return new JobBuilder("job-bulk-insert", jobRepository)
                .start(bulkInsertFlow).end().listener(jobCompletionListener).build();
    }
}
