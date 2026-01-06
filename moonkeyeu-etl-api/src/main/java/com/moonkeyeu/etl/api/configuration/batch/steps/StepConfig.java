package com.moonkeyeu.etl.api.configuration.batch.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.configuration.batch.listeners.StepContextSetter;
import com.moonkeyeu.etl.api.configuration.batch.processors.ChunkProcessor;
import com.moonkeyeu.etl.api.configuration.batch.readers.JsonItemReader;
import com.moonkeyeu.etl.api.configuration.batch.writers.ItemWriterRegistry;
import com.moonkeyeu.etl.api.configuration.files.CsvManager;
import com.moonkeyeu.etl.api.configuration.files.FilePathProvider;
import com.moonkeyeu.etl.api.configuration.files.RootConfig;
import com.moonkeyeu.etl.api.configuration.url.UrlBuilderConfig;
import com.moonkeyeu.etl.api.dto.chunks.ChunkStore;
import com.moonkeyeu.etl.api.dto.EntityConfig;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.service.client.ClientDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.FileNotFoundException;
import java.util.*;
import static com.moonkeyeu.etl.api.configuration.files.JsonGroup.JSON_AGENCIES;
import static com.moonkeyeu.etl.api.configuration.files.JsonGroup.JSON_LAUNCHES;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final CsvManager csvManager;
    private final RootConfig rootConfig;
    private final FilePathProvider filePathProvider;
    private final ChunkProcessor chunkProcessor;
    private final ItemProcessor<JsonNode, ChunkStore> launchesProcessor;
    private final ItemProcessor<JsonNode, ChunkStore> agenciesProcessor;
    private final ItemWriter<Object> jpaEntityWriter;
    private final ItemWriter<ChunkStore> itemWriter;
    private final ItemWriterRegistry itemWriterRegistry;
    private final JsonItemReader jsonItemReader;
    private final ItemReader<CsvEntity<?>> itemReader;
    private final ClientDataService clientDataService;
    private final UrlBuilderConfig urlBuilderConfig;
    private final int CHUNK_SIZE = 150;

    @Bean
    public Step fetchLatestDataStep() {
        return new StepBuilder("fetchLatestDataStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String launchesJsonFile = filePathProvider.getJsonSource(JSON_LAUNCHES.getJsonFile());
                    clientDataService.fetchData(urlBuilderConfig.getLaunchesUrlForWindow(), launchesJsonFile).block();
                    //log.info("Successfully fetched latest data. Between " + windowsStart + " and " + windowEnd );
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .transactionManager(platformTransactionManager)
                .build();
    }

    @Bean
    public Step fetchAllLatestDataStep() {
        return new StepBuilder("fetchAllLatestDataStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String launchesJsonFile = filePathProvider.getJsonSource(JSON_LAUNCHES.getJsonFile());
                    clientDataService.fetchData(urlBuilderConfig.getAllLatestLaunchesUrl(), launchesJsonFile).block();
                    //log.info("Successfully fetched all latest data. window start: " + windowsStart );
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .transactionManager(platformTransactionManager)
                .build();
    }

    @Bean
    public Step fetchAgenciesDataStep() {
        return new StepBuilder("fetchAgenciesStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String agenciesJsonFile = filePathProvider.getJsonSource(JSON_AGENCIES.getJsonFile());
                    clientDataService.fetchData(urlBuilderConfig.baseAgenciesUrl(), agenciesJsonFile).block();
                    log.info("Successfully fetched JSON data.");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .transactionManager(platformTransactionManager)
                .build();
    }

    @Bean
    public Step fetchLaunchesDataStep() {
        return new StepBuilder("fetchLaunchesStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String launchesJsonFile = filePathProvider.getJsonSource(JSON_LAUNCHES.getJsonFile());
                    clientDataService.fetchData(urlBuilderConfig.baseLaunchesUrl(), launchesJsonFile).block();
                    log.info("Successfully fetched JSON data.");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .transactionManager(platformTransactionManager)
                .build();
    }

    @Bean
    public Step cleanupStep() {
        return new StepBuilder("cleanupStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Map<String, Object> jobParametersMap = chunkContext.getStepContext().getJobParameters();
                    boolean skipCsv = Boolean.parseBoolean((String) jobParametersMap.getOrDefault("skipCsv", "false"));
                    boolean skipJson = Boolean.parseBoolean((String) jobParametersMap.getOrDefault("skipJson", "false"));
                    if (!skipCsv) {
                        boolean isDeleted = csvManager.deleteAllFiles(rootConfig.getCsvRootFolder());
                        if (!isDeleted) {
                            contribution.setExitStatus(ExitStatus.FAILED);
                        }
                    }
                    if (!skipJson) {
                        boolean isDeleted = csvManager.deleteAllFiles(rootConfig.getJsonRootFolder());
                        if (!isDeleted) {
                            contribution.setExitStatus(ExitStatus.FAILED);
                        }
                    }
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .transactionManager(platformTransactionManager)
                .build();
    }

    @Bean
    public Step readLaunchesJsonStep() throws FileNotFoundException {
        String launchesJsonFile = filePathProvider.getJsonSource(JSON_LAUNCHES.getJsonFile());
        if (launchesJsonFile.isBlank()) throw new FileNotFoundException("Json file not found.");
        return new StepBuilder("process_launches", jobRepository)
                .<JsonNode, ChunkStore>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(jsonItemReader)
                .processor(launchesProcessor)
                .writer(itemWriter)
                .stream(itemWriterRegistry)
                .faultTolerant()
                .retry(WebClientResponseException.class)
                .retryLimit(2)
                .listener(new StepContextSetter<>("jsonFilePath", launchesJsonFile))
                .build();
    }

    @Bean
    public Step readAgenciesJsonStep() {
        String agenciesJsonFile = filePathProvider.getJsonSource(JSON_AGENCIES.getJsonFile());
        return new StepBuilder("process_agencies", jobRepository)
                .<JsonNode, ChunkStore>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(jsonItemReader)
                .processor(agenciesProcessor)
                .writer(itemWriter)
                .stream(itemWriterRegistry)
                .faultTolerant()
                .retry(WebClientResponseException.class)
                .retryLimit(2)
                .listener(new StepContextSetter<>("jsonFilePath", agenciesJsonFile))
                .build();
    }
    /**
     * KNOWN ISSUE: Duplicate Step Names
     * <p>
     * Both latestLaunchesFlow and agenciesFlow reuse importToDatabaseFlow, which creates
     * steps with identical names (e.g., "process_CountryEntity_1"). This triggers a Spring
     * Batch warning about duplicate steps affecting restart behavior.
     * <p>
     * Current status: Acceptable - steps are not restartable by design and always run from
     * scratch. The warning is informational and doesn't affect normal operation.
     * <p>
     * Future fix: Pass flow context to createStepForEntity() to generate unique step names
     * like "latestLaunches_process_CountryEntity_1" vs "agencies_process_CountryEntity_1"
     * <p>
     * Related: CreateStepForEntity.createStepForEntity(), importToDatabaseFlow()
     */
    public Step createImportStep(EntityConfig config) {
        String stepName = "process_" + config.getEntityClass().getSimpleName() + "_" + config.getOrder();
        return new StepBuilder(stepName, jobRepository).<CsvEntity<?>, CsvEntity<?>>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(itemReader)
                .processor(chunkProcessor)
                .writer(jpaEntityWriter)
                .listener(new StepContextSetter<>("entityConfig", config))
                .transactionManager(platformTransactionManager)
                .build();
    }
}
