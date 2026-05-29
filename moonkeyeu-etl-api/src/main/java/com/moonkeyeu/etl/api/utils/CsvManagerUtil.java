package com.moonkeyeu.etl.api.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Slf4j
@Component
public class CsvManagerUtil {
    public boolean deleteAllFiles(String folderPath) {
        Path folder = Paths.get(folderPath);
        if (!Files.exists(folder) || !Files.isDirectory(folder)) {
            log.error("Invalid path: {} is not a directory or does not exist.", folderPath);
            return false;
        }

        try (Stream<Path> files = Files.walk(folder)) {
            files.filter(Files::isRegularFile).forEach(this::deleteFile);
            return true;
        } catch (IOException e) {
            log.error("An error occurred while reading the directory: " + folderPath, e);
            return false;
        }
    }
    private void deleteFile(Path file) {
        try {
            boolean deleted = Files.deleteIfExists(file);
            if (deleted) {
                log.debug("Deleted file: {}", file.getFileName());
            } else {
                log.error("Failed to delete file: {}", file.getFileName());
            }
        } catch (IOException e) {
            log.error("Error deleting file: {}", file.getFileName(), e);
        }
    }
}
