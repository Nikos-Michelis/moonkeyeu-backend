package com.moonkeyeu.etl.api.configuration.batch.readers;

import com.fasterxml.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.dto.storage.EntityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.MultiResourceItemReader;
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
    public CustomItemReader itemReader(@Value("#{stepExecutionContext['entityConfig']}") EntityConfig config) {
        CustomItemReader reader = new CustomItemReader();
        reader.setType(config.getEntityClass());
        reader.setResource(new FileSystemResource(config.getFileName()));
        return reader;
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<JsonNode> multiResourceItemReader(@Value("#{stepExecutionContext['jsonFolderPath']}") String folderPath) throws IOException {
        MultiResourceItemReader<JsonNode> multiResourceItemReader = new MultiResourceItemReader<>();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("file:" + folderPath + "/*.json");
        JsonItemReader jsonItemReader = new JsonItemReader();
        multiResourceItemReader.setResources(resources);
        multiResourceItemReader.setDelegate(jsonItemReader);
        return multiResourceItemReader;
    }
}
