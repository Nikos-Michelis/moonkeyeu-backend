package com.moonkeyeu.etl.api.configuration.batch.readers;

import com.fasterxml.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidFileTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JsonItemReaderTest {
    @InjectMocks
    @Spy
    private JsonItemReader jsonItemReader;
    private ClassPathResource resource;
    private ExecutionContext executionContext;

    @BeforeEach
    void setUp() {
        jsonItemReader = new JsonItemReader();
        executionContext = new ExecutionContext();
        resource = new ClassPathResource("allResults.json");
    }

    @Test
    @DisplayName("Should open valid JSON file successfully")
    void open_shouldInitializeParserSuccessfully() {
        // Given
        jsonItemReader.setResource(resource);
        // when
        jsonItemReader.open(executionContext);
        // then
        assertThat(jsonItemReader.getParser()).isNotNull();
    }

    @Test
    @DisplayName("Should throw exception when file type is invalid")
    void open_shouldThrowException_whenInvalidFileType() {
        // given
        JsonItemReader reader = new JsonItemReader();
        reader.setResource(new ClassPathResource("invalid.txt"));
        // when - then
        assertThatThrownBy(() ->
                reader.open(executionContext))
                .isInstanceOf(InvalidFileTypeException.class)
                .hasMessageContaining("Invalid file type");
    }

    @Test
    @DisplayName("Should read all JSON objects from all_results array")
    void read_shouldReturnJsonNodes() throws Exception {
        // given
        jsonItemReader.setResource(resource);
        // when
        jsonItemReader.open(executionContext);
        JsonNode first = jsonItemReader.read();
        JsonNode second = jsonItemReader.read();
        JsonNode third = jsonItemReader.read();

        // then
        assertThat(first).isNotNull();
        assertNotNull(first);
        assertThat(first.get("id").asText()).isEqualTo("a0058cfb-c868-44b6-82cb-7f4ed286b272");
        assertThat(first.get("name").asText()).isEqualTo("Falcon 9 Block 5 | Starlink Group 6-80");

        assertThat(first.size()).isEqualTo(39);

        jsonItemReader.close();
    }

    @Test
    @DisplayName("Should return null when all_results array is empty")
    void read_shouldReturnNull_whenArrayEmpty() throws Exception {
        // given
        JsonItemReader reader = new JsonItemReader();
        reader.setResource(new ClassPathResource("json/empty-results.json"));
        // when
        reader.open(new ExecutionContext());
        JsonNode result = reader.read();
        // then
        assertThat(result).isNull();
        reader.close();
    }

    @Test
    @DisplayName("Should return null when parser is not initialized")
    void read_shouldReturnNull_whenParserIsNull() throws Exception {
        // given
        JsonItemReader reader = new JsonItemReader();
        // when
        JsonNode result = reader.read();
        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should close parser successfully")
    void close_shouldCloseParserSuccessfully() {
        // given
        jsonItemReader.setResource(resource);
        // when
        jsonItemReader.open(executionContext);
        // then
        jsonItemReader.close();
        assertThat(jsonItemReader.getParser().isClosed()).isTrue();
    }

    @Test
    @DisplayName("Should not fail when parser is null during close")
    void close_shouldDoNothing_whenParserIsNull() {
        // when
        jsonItemReader.close();
        // then
        assertThat(jsonItemReader.getParser()).isNull();
    }

    @Test
    @DisplayName("Should skip opening when file does not exist")
    void open_shouldSkip_whenFileDoesNotExist() {
        // given
        jsonItemReader.setResource(
                new ClassPathResource("json/missing.json")
        );
        // when
        jsonItemReader.open(executionContext);
        // then
        assertThat(jsonItemReader.getParser()).isNull();
    }

}