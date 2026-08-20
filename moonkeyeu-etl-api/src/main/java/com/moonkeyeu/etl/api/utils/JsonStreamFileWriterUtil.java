package com.moonkeyeu.etl.api.utils;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonStreamFileWriterUtil {
    private final ObjectMapper objectMapper;

    public Mono<JsonGenerator> open(String fileName) {
        return Mono.fromCallable(() -> {
            Path path = Paths.get(fileName);

            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            JsonGenerator generator = objectMapper
                    .getFactory()
                    .createGenerator(path.toFile(), JsonEncoding.UTF8);

            generator.writeStartObject();
            generator.writeArrayFieldStart("all_results");

                    return generator;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> write(JsonGenerator generator, JsonNode response) {
        if (response.has("detail")) {
            log.warn("Skipping response with error detail: {}", response.get("detail").asText());
            return Mono.empty();
        }

        return Mono.fromRunnable(() -> {
            try {
                generator.writeObject(response);
            } catch (IOException e) {
                throw new RuntimeException("Error writing response", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    public void close(JsonGenerator generator) {
        try {
            generator.writeEndArray();
            generator.writeEndObject();
            generator.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing generator: " + e.getMessage());
        }
    }
}
