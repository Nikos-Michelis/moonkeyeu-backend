package com.moonkeyeu.etl.api.configuration.batch.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moonkeyeu.etl.api.configuration.files.CsvSource;
import com.moonkeyeu.etl.api.configuration.files.FilePathProvider;
import com.moonkeyeu.etl.api.configuration.files.RootConfig;
import com.moonkeyeu.etl.api.configuration.mappers.JsonObjectMapper;
import com.moonkeyeu.etl.api.configuration.s3.S3Buckets;
import com.moonkeyeu.etl.api.dto.chunks.ChunkStore;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.service.LocalMediaService;
import com.moonkeyeu.etl.api.service.S3MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProcessorsConfig {
    private final S3MediaService s3MediaService;
    private final LocalMediaService localMediaService;
    private final S3Buckets s3Buckets;
    private final RootConfig rootConfig;
    private final FilePathProvider filePathProvider;

    @Bean
    public CompositeItemProcessor<CsvEntity<?>, CsvEntity<?>> compositeProcessor() {
        CompositeItemProcessor<CsvEntity<?>, CsvEntity<?>> processor = new CompositeItemProcessor<>();
        processor.setDelegates(List.of(cleaningProcessor(), mediaProcessor()));
        return processor;
    }

    @Bean
    public CleaningProcessor cleaningProcessor() {
        return new CleaningProcessor();
    }

    @Bean
    @StepScope
    public MediaProcessor mediaProcessor() {
        return new MediaProcessor(localMediaService, s3MediaService, rootConfig, filePathProvider, s3Buckets);
    }

    @Bean
    public ItemProcessor<JsonNode, ChunkStore> launchesProcessor(ObjectMapper objectMapper) {
        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper(objectMapper);
        return jsonNode -> {
            ChunkStore chunk = jsonObjectMapper.jsonToLaunchesMapper(jsonNode, new ChunkStore());
            chunk.add(chunk.getLaunches(), CsvSource.RAW_LAUNCHES_CSV);
            chunk.add(chunk.getPadImages(), CsvSource.RAW_PAD_IMAGES_CSV);
            chunk.add(chunk.getRocketConfImages(), CsvSource.RAW_ROCKET_CONFIGURATION_IMAGES_CSV);
            chunk.add(chunk.getSpacecraftConfImages(), CsvSource.RAW_SPACECRAFT_IMAGES_CSV);
            chunk.add(chunk.getAstronautImages(), CsvSource.RAW_ASTRONAUT_IMAGES_CSV);
            chunk.add(chunk.getLauncherStage(), CsvSource.RAW_BOOSTERS_CSV);
            chunk.add(chunk.getSpacecraftStage(), CsvSource.RAW_SPACECRAFT_STAGE_CSV);
            chunk.add(chunk.getCrewList(), CsvSource.RAW_ASTRONAUTS_CSV);
            chunk.add(chunk.getSocialMedia(), CsvSource.RAW_SOCIAL_MEDIA_CSV);
            chunk.add(chunk.getNationalities(), CsvSource.ASTRONAUTS_COUNTRIES_CSV);
            chunk.add(chunk.getCountries(), CsvSource.RAW_ASTRONAUTS_COUNTRIES_CSV);
            chunk.add(chunk.getVideoList(), CsvSource.RAW_VIDEO_CSV);
            chunk.add(chunk.getUpdates(), CsvSource.RAW_UPDATES_CSV);
            chunk.add(chunk.getPrograms(), CsvSource.RAW_PROGRAMS_CSV);
            chunk.add(chunk.getProgramHasAgencies(), CsvSource.PROGRAMS_AGENCIES_CSV);
            chunk.add(chunk.getPatches(), CsvSource.RAW_MISSION_PATCHES_CSV);
            chunk.add(chunk.getInfoUrls(), CsvSource.RAW_INFO_URLS_CSV);
            chunk.add(chunk.getAgencies(), CsvSource.RAW_MISSIONS_AGENCIES_CSV);
            return chunk;
        };
    }

    @Bean
    public ItemProcessor<JsonNode, ChunkStore> agenciesProcessor(ObjectMapper objectMapper) {
        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper(objectMapper);
        return jsonNode -> {
            ChunkStore chunk = jsonObjectMapper.JsonToAgenciesMapper(jsonNode, new ChunkStore());
            chunk.add(chunk.getAgencies(), CsvSource.RAW_AGENCIES_CSV);
            chunk.add(chunk.getAgenciesImages(), CsvSource.RAW_AGENCIES_IMAGES_CSV);
            chunk.add(chunk.getCountries(), CsvSource.RAW_AGENCY_COUNTRIES_CSV);
            chunk.add(chunk.getAgencyHasCountries(), CsvSource.LAUNCH_PROVIDERS_COUNTRIES_CSV);
            return chunk;
        };
    }
}
