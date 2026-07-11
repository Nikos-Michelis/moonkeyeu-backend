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
    public MultiResourceItemReader<JsonNode> jsonMultiResourceItemReader(@Value("#{stepExecutionContext['jsonFolderPath']}") Resource[] fileResources) {
        MultiResourceItemReader<JsonNode> multiResourceItemReader = new MultiResourceItemReader<>();
        JsonItemReader jsonItemReader = new JsonItemReader();
        multiResourceItemReader.setDelegate(jsonItemReader);
        multiResourceItemReader.setResources(fileResources);
        return multiResourceItemReader;
    }

   /* @Bean
    @StepScope
    public JsonItemReader jsonItemReader(@Value("#{stepExecutionContext['jsonFilePath']}") String jsonFilePath) {
        JsonItemReader jsonItemReader = new JsonItemReader();
        jsonItemReader.setResource(new FileSystemResource(jsonFilePath));
        return jsonItemReader;
    }*/

}
