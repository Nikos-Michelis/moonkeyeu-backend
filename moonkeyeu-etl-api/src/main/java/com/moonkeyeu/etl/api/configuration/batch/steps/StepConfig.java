package com.moonkeyeu.etl.api.configuration.batch.steps;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.configuration.batch.listeners.StepCompletionListener;
import com.moonkeyeu.etl.api.configuration.batch.listeners.StepContextSetter;
import com.moonkeyeu.etl.api.configuration.files.FilePathProvider;
import com.moonkeyeu.etl.api.configuration.files.RootConfig;
import com.moonkeyeu.etl.api.configuration.batch.jobs.CleanupType;
import com.moonkeyeu.etl.api.configuration.batch.jobs.StorageType;
import com.moonkeyeu.etl.api.configuration.batch.jobs.StoreOperation;
import com.moonkeyeu.etl.api.pipeline.core.RowSink;
import com.moonkeyeu.etl.api.pipeline.ll2.media.MediaMigrationService;
import com.moonkeyeu.etl.api.configuration.batch.writers.UpsertWriter;
import com.moonkeyeu.etl.api.service.ClientDataService;
import com.moonkeyeu.etl.api.settings.exceptions.CleanupException;
import com.moonkeyeu.etl.api.utils.FileManagerUtil;
import com.moonkeyeu.etl.api.utils.LL2URIBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.file.MultiResourceItemReader;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;

import static com.moonkeyeu.etl.api.configuration.files.JsonGroup.JSON_AGENCIES;
import static com.moonkeyeu.etl.api.configuration.files.JsonGroup.JSON_LAUNCHES;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StepConfig {

    private static final int CHUNK_SIZE = 500;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final FileManagerUtil fileManagerUtil;
    private final RootConfig rootConfig;
    private final FilePathProvider filePathProvider;
    private final ItemProcessor<JsonNode, RowSink> launchesProcessor;
    private final ItemProcessor<JsonNode, RowSink> agenciesProcessor;
    private final UpsertWriter upsertWriter;
    private final MultiResourceItemReader<JsonNode> multiResourceItemReader;
    private final ClientDataService clientDataService;
    private final LL2URIBuilder LL2URIBuilder;
    private final MediaMigrationService mediaMigrationService;

    @Bean
    public Step fetchYearlyLaunchesStep() {
        LocalDate windowStart = LocalDate.now().minusMonths(1);
        LocalDate windowEnd =  LocalDate.now().plusMonths(12);
        URI uri = LL2URIBuilder.launchesBetweenURI(windowStart, windowEnd);
        String launchesJsonFile = filePathProvider.getJsonSource(JSON_LAUNCHES.getFolder(), JSON_LAUNCHES.getFile());

        return new StepBuilder("step-fetch-yearly-launches", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    clientDataService.fetchAll(uri, launchesJsonFile).block();
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .transactionManager(platformTransactionManager)
                .build();
    }

    @Bean
    public Step fetchAllLaunchesStep() {
        LocalDate windowStart = LocalDate.now().minusYears(5);
        URI uri = LL2URIBuilder.launchesFromURI(windowStart);
        String launchesJsonFile = filePathProvider.getJsonSource(JSON_LAUNCHES.getFolder(), JSON_LAUNCHES.getFile());

        return new StepBuilder("step-fetch-all-launches", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    clientDataService.fetchAll(uri, launchesJsonFile).block();
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .transactionManager(platformTransactionManager)
                .build();
    }

    @Bean
    public Step fetchAgenciesStep() {
        String agenciesJsonFile = filePathProvider.getJsonSource(JSON_AGENCIES.getFolder(), JSON_AGENCIES.getFile());
        URI uri = LL2URIBuilder.allAgenciesURI();
        return new StepBuilder("step-fetch-agencies", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    clientDataService.fetchAll(uri, agenciesJsonFile).block();
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .listener(new StepCompletionListener())
                .transactionManager(platformTransactionManager)
                .build();
    }

    @Bean
    public Step cleanupStep() {
        return new StepBuilder("step-cleanup", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                    Object cleanup = jobParameters.get("cleanup");

                    if (cleanup == null) {
                        throw new CleanupException("Cleanup step requires parameters for cleanup");
                    }

                    switch (CleanupType.from(cleanup.toString())) {
                        case ALL -> fileManagerUtil.deleteAllFiles(rootConfig.getJsonRootFolder());
                        case NONE -> log.debug("Cleanup skipped; existing JSON retained");
                    }

                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .listener(new StepCompletionListener())
                .transactionManager(platformTransactionManager)
                .build();
    }

    @Bean
    public Step launchesETLStep() {
        return loadStep("step-load-launches", JSON_LAUNCHES.getFolder(), launchesProcessor);
    }

    @Bean
    public Step agenciesETLStep() {
        return loadStep("step-load-agencies", JSON_AGENCIES.getFolder(), agenciesProcessor);
    }

    private Step loadStep(String name, String jsonFolder, ItemProcessor<JsonNode, RowSink> processor) {
        String folderPath = filePathProvider.getJsonDir(jsonFolder);
        if (folderPath.isBlank()) {
            throw new IllegalStateException("Json folder not found for " + jsonFolder);
        }
        return new StepBuilder(name, jobRepository)
                .<JsonNode, RowSink>chunk(CHUNK_SIZE)
                .transactionManager(platformTransactionManager)
                .reader(multiResourceItemReader)
                .processor(processor)
                .writer(upsertWriter)
                .faultTolerant()
                .retry(WebClientResponseException.class)
                .retryLimit(2)
                .listener(new StepContextSetter<>("jsonFolderPath", folderPath))
                .listener(new StepCompletionListener())
                .build();
    }

    @Bean
    public Step mediaStep() {
        return new StepBuilder("step-migrate-media", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                    StorageType storage = StorageType.from(String.valueOf(jobParameters.get("storage")));
                    StoreOperation operation = StoreOperation.from(String.valueOf(jobParameters.get("operation")));

                    int migrated = mediaMigrationService.migrate(storage, operation);
                    contribution.incrementWriteCount(migrated);
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .listener(new StepCompletionListener())
                .transactionManager(platformTransactionManager)
                .build();
    }
}
