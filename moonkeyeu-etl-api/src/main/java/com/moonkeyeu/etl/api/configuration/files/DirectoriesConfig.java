package com.moonkeyeu.etl.api.configuration.files;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
public class DirectoriesConfig {

    private final FilePathProvider filePathProvider;

    public DirectoriesConfig(FilePathProvider filePathProvider) {
        this.filePathProvider = filePathProvider;
    }

    @PostConstruct
    public void createJsonDirectories() {
        for (JsonGroup group : JsonGroup.values()) {
            Path dir = Paths.get(filePathProvider.getJsonDir(group.getFolder()));
            if (Files.isDirectory(dir)) {
                log.info("JSON directory already exists for {}: {}", group, dir.toAbsolutePath());
                continue;
            }
            try {
                Files.createDirectories(dir);
                log.info("JSON directory created for {}: {}", group, dir.toAbsolutePath());
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to create JSON directory for " + group + ": " + dir, e);
            }
        }
    }
}
