package com.moonkeyeu.etl.api.configuration.batch.writers;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.moonkeyeu.etl.api.configuration.files.CsvSource;
import com.moonkeyeu.etl.api.configuration.files.FilePathProvider;
import com.moonkeyeu.etl.api.dto.chunks.ChunkStore;
import com.moonkeyeu.etl.api.repository.persistence.GenericPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WritersConfig {
    private final FilePathProvider filePathProvider;
    private final GenericPersistenceService genericPersistenceService;


    @Bean
    public ItemWriter<Object> jpaEntityWriter() {
        return genericPersistenceService::saveAll;
    }

    @Bean
    public ItemWriter<ChunkStore> itemWriter(CsvMapper csvMapper) {
        return chunks -> {
            chunks.getItems().forEach(chunk -> {
                chunk.getTasks().forEach(task -> {
                    CustomItemWriter writer = itemWriterRegistry(csvMapper).get(task.target());
                    writer.write(task.data());
                });
            });
        };
    }

    @Bean
    public ItemWriterRegistry itemWriterRegistry(CsvMapper csvMapper) {
        ItemWriterRegistry registry = new ItemWriterRegistry();
        for (CsvSource source : CsvSource.values()) {
            CustomItemWriter writer = new CustomItemWriter(csvMapper);
            writer.setResource(new FileSystemResource(filePathProvider.getCsvSource(source.getCsvFile())));
            registry.register(source, writer);
        }
        return registry;
    }
}
