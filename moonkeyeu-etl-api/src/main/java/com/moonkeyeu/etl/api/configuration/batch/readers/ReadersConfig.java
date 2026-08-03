package com.moonkeyeu.etl.api.configuration.batch.readers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.moonkeyeu.etl.api.dto.storage.EntityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ReadersConfig {

    @Bean
    @StepScope
    public CustomItemReader itemReader(@Value("#{stepExecutionContext['entityConfig']}") EntityConfig config, CsvMapper csvMapper) {
        CustomItemReader reader = new CustomItemReader(csvMapper);
        reader.setType(config.getEntityClass());
        reader.setResource(new FileSystemResource(config.getFileName()));
        return reader;
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<JsonNode> multiResourceItemReader(@Value("#{stepExecutionContext['jsonFolderPath']}") String folderPath, ObjectMapper objectMapper) throws IOException {
        JsonItemReader jsonItemReader = new JsonItemReader(objectMapper);
        MultiResourceItemReader<JsonNode> multiResourceItemReader = new MultiResourceItemReader<>(jsonItemReader);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("file:" + folderPath + "/*.json");
        multiResourceItemReader.setResources(resources);
        return multiResourceItemReader;
    }
}
