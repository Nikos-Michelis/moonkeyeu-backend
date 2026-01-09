package com.moonkeyeu.etl.api.configuration.batch.flows;

import com.moonkeyeu.etl.api.configuration.batch.steps.StepConfig;
import com.moonkeyeu.etl.api.configuration.files.FilePathProvider;
import com.moonkeyeu.etl.api.dto.EntityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.LinkedList;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FlowConfig {
    private final Step readAgenciesJsonStep;
    private final Step fetchAgenciesDataStep;
    private final Step fetchAllLatestDataStep;
    private final Step readLaunchesJsonStep;
    private final Step fetchLatestDataStep;
    private final Step cleanupStep;
    private final StepConfig createStepForEntity;
    private final FilePathProvider filePathProvider;

    @Bean
    public Flow agenciesFlow() {
        return new FlowBuilder<Flow>("agenciesFlow")
                .start(cleanupStep)
                .next(fetchAgenciesDataStep)
                .next(readAgenciesJsonStep)
                .next(importToDatabaseFlow())
                .build();
    }

    @Bean
    public Flow allLatestLaunchesFlow() {
        return new FlowBuilder<Flow>("allLatestLaunchesFlow")
                .start(cleanupStep)
                .next(fetchAllLatestDataStep)
                .next(readLaunchesJsonStep)
                .next(importToDatabaseFlow())
                .build();
    }

    @Bean
    public Flow latestLaunchesUntilFlow() {
        return new FlowBuilder<Flow>("latestLaunchesUntilFlow")
                .start(cleanupStep)
                .next(fetchLatestDataStep)
                .next(readLaunchesJsonStep)
                .next(importToDatabaseFlow())
                .build();
    }

    @Bean
    public Flow bulkInsertFlow() {
        return new FlowBuilder<Flow>("bulkInsertFlow")
                .start(cleanupStep)
                .next(readAgenciesJsonStep)
                .next(readLaunchesJsonStep)
                .next(importToDatabaseFlow())
                .build();
    }

    @Bean
    public Flow importToDatabaseFlow() {
        LinkedList<EntityConfig> configs = filePathProvider.getCsvGroups();
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("importToDatabaseFlow");
        if (!configs.isEmpty()) {
            flowBuilder.start(createStepForEntity.createImportStep(configs.get(0)));
            for (int i = 1; i < configs.size(); i++) {
                flowBuilder.next(createStepForEntity.createImportStep(configs.get(i)));
            }
        }
        return flowBuilder.build();
    }
}
