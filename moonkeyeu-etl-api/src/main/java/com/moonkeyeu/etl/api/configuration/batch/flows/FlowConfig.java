package com.moonkeyeu.etl.api.configuration.batch.flows;

import com.moonkeyeu.etl.api.configuration.batch.steps.StepConfig;
import com.moonkeyeu.etl.api.configuration.files.FilePathProvider;
import com.moonkeyeu.etl.api.dto.storage.EntityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.LinkedList;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FlowConfig {
    private final Step agenciesETLStep;
    private final Step fetchAgenciesStep;
    private final Step fetchAllLaunchesStep;
    private final Step launchesETLStep;
    private final Step fetchYearlyLaunchesStep;
    private final Step cleanupStep;
    private final StepConfig createStepForEntity;
    private final FilePathProvider filePathProvider;

    @Bean
    public Flow syncAllAgenciesFlow() {
        return new FlowBuilder<Flow>("flow-all-agencies")
                .start(cleanupStep)
                .next(fetchAgenciesStep)
                .next(agenciesETLStep)
                .next(importCsvDataFlow())
                .build();
    }

    @Bean
    public Flow syncYealyLaunchesFlow() {
        return new FlowBuilder<Flow>("flow-yearly-launches")
                .start(cleanupStep)
                .next(fetchYearlyLaunchesStep)
                .next(launchesETLStep)
                .next(importCsvDataFlow())
                .build();
    }

    @Bean
    public Flow syncAllLaunchesFlow() {
        return new FlowBuilder<Flow>("flow-all-launches")
                .start(cleanupStep)
                .next(fetchAllLaunchesStep)
                .next(launchesETLStep)
                .next(importCsvDataFlow())
                .build();
    }

    @Bean
    public Flow bulkInsertFlow() {
        return new FlowBuilder<Flow>("flow-bulk-insert")
                .start(cleanupStep)
                .next(agenciesETLStep)
                .next(launchesETLStep)
                .next(importCsvDataFlow())
                .build();
    }

    @Bean
    public Flow importCsvDataFlow() {
        LinkedList<EntityConfig> configs = filePathProvider.getCsvGroups();
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow-import-csv");
        if (!configs.isEmpty()) {
            flowBuilder.start(createStepForEntity.createImportStep(configs.getFirst()));
            for (int i = 1; i < configs.size(); i++) {
                flowBuilder.next(createStepForEntity.createImportStep(configs.get(i)));
            }
        }
        return flowBuilder.build();
    }
}
