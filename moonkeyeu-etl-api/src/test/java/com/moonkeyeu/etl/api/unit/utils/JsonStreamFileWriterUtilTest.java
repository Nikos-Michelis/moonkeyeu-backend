package com.moonkeyeu.etl.api.unit.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moonkeyeu.etl.api.utils.JsonStreamFileWriterUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class JsonStreamFileWriterUtilTest {

    @Mock
    private JsonGenerator generator;
    private ObjectMapper objectMapper;
    private JsonStreamFileWriterUtil jsonStreamFileWriterUtil;
    @TempDir
    private Path tempDir;
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        jsonStreamFileWriterUtil = new JsonStreamFileWriterUtil(objectMapper);
    }

    @Test
    @DisplayName("Should create JsonGenerator and initialize JSON structure")
    void open_shouldCreateJsonGeneratorAndInitializeJsonStructure() {
        // given
        String fileName = tempDir.resolve("test.json").toString();
        // when
        StepVerifier.create(jsonStreamFileWriterUtil.open(fileName))
                .assertNext(generator -> {
                    assertThat(generator).isNotNull();
                    jsonStreamFileWriterUtil.close(generator);
                })
                .verifyComplete();

        assertThat(Files.exists(Path.of(fileName))).isTrue();
    }

    @Test
    @DisplayName("Should write JSON object into all_results array")
    void write_shouldWriteJsonObject() throws IOException {
        // given
        String fileName = tempDir.resolve("write-test.json").toString();
        JsonGenerator generator =
                jsonStreamFileWriterUtil.open(fileName).block();
        JsonNode node = objectMapper.readTree("""
                {
                    "id": 1,
                    "name": "test_name"
                }
                """);

        // when
        StepVerifier.create(jsonStreamFileWriterUtil.write(generator, node))
                .verifyComplete();

        assertNotNull(generator);
        jsonStreamFileWriterUtil.close(generator);

        // then
        String content = Files.readString(Path.of(fileName));

        assertThat(content)
                .contains("\"all_results\"")
                .contains("\"id\":1")
                .contains("\"name\":\"test_name\"");
    }

    @Test
    @DisplayName("Should skip writing response when detail field exists")
    void write_shouldSkipResponseWhenDetailExists() throws IOException {

        // given
        String fileName = tempDir.resolve("skip-test.json").toString();

        JsonGenerator generator =
                jsonStreamFileWriterUtil.open(fileName).block();

        JsonNode node = objectMapper.readTree("""
                {
                    "detail": "Something went wrong"
                }
                """);

        // when
        StepVerifier.create(jsonStreamFileWriterUtil.write(generator, node))
                .verifyComplete();

        assertNotNull(generator);
        jsonStreamFileWriterUtil.close(generator);

        // then
        String content = Files.readString(Path.of(fileName));

        assertThat(content)
                .contains("\"all_results\":[]");
    }

    @Test
    @DisplayName("Should close JSON structure properly")
    void close_shouldCloseJsonProperly() throws IOException {

        // given
        String fileName = tempDir.resolve("close-test.json").toString();

        JsonGenerator generator =
                jsonStreamFileWriterUtil.open(fileName).block();

        // when
        assertNotNull(generator);
        jsonStreamFileWriterUtil.close(generator);

        // then
        String content = Files.readString(Path.of(fileName));

        assertThat(content)
                .isEqualTo("""
                        {"all_results":[]}
                        """.trim());
    }

    @Test
    @DisplayName("Should throw RuntimeException when generator close fails")
    void close_shouldThrowRuntimeException_whenGeneratorFails() throws IOException {

        doThrow(new IOException("Something went wrong"))
                .when(generator)
                .writeEndArray();

        assertThrows(RuntimeException.class, () ->
                jsonStreamFileWriterUtil.close(generator));
    }
}