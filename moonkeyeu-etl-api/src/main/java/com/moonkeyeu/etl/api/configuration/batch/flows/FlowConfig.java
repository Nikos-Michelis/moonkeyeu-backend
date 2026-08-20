package com.moonkeyeu.etl.api.configuration.batch.flows;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Every flow is now fetch → load → media.
 * <p>
 * {@code importCsvDataFlow} is gone. It generated fifty-three steps from {@code CsvGroup}, each one
 * a full scan of a CSV file for a single target table, and because all four flows reused it the
 * generated steps collided on name — the duplicate-step warning documented in the old
 * {@code StepConfig} javadoc. Both the flow and the warning disappear with the CSV layer.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class FlowConfig {

    private final Step cleanupStep;
    private final Step fetchAgenciesStep;
    private final Step agenciesETLStep;
    private final Step fetchAllLaunchesStep;
    private final Step fetchYearlyLaunchesStep;
    private final Step launchesETLStep;
    private final Step mediaStep;

    @Bean
    public Flow syncAllAgenciesFlow() {
        return new FlowBuilder<Flow>("flow-all-agencies")
                .start(cleanupStep)
                .next(fetchAgenciesStep)
                .next(agenciesETLStep)
                .next(mediaStep)
                .build();
    }

    @Bean
    public Flow syncYealyLaunchesFlow() {
        return new FlowBuilder<Flow>("flow-yearly-launches")
                .start(cleanupStep)
                .next(fetchYearlyLaunchesStep)
                .next(launchesETLStep)
                .next(mediaStep)
                .build();
    }

    @Bean
    public Flow syncAllLaunchesFlow() {
        return new FlowBuilder<Flow>("flow-all-launches")
                .start(cleanupStep)
                .next(fetchAllLaunchesStep)
                .next(launchesETLStep)
                .next(mediaStep)
                .build();
    }

    @Bean
    public Flow bulkInsertFlow() {
        return new FlowBuilder<Flow>("flow-bulk-insert")
                .start(cleanupStep)
                .next(agenciesETLStep)
                .next(launchesETLStep)
                .next(mediaStep)
                .build();
    }
}
