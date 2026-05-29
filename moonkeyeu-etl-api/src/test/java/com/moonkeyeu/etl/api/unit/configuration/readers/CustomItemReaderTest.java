package com.moonkeyeu.etl.api.unit.configuration.readers;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.moonkeyeu.etl.api.config.TestEntity;
import com.moonkeyeu.etl.api.configuration.batch.readers.CustomItemReader;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidFileTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomItemReaderTest {

    @Mock
    private MappingIterator<CsvEntity<?>> iterator;
    @Mock
    private BufferedReader bufferedReader;
    @InjectMocks
    @Spy
    private CustomItemReader customItemReader;
    private ExecutionContext executionContext;
    @TempDir
    Path tempDir;


    @BeforeEach
    void setUp() {
        customItemReader = new CustomItemReader();
        customItemReader.setType(TestEntity.class);
        customItemReader.setIterator(iterator);
        customItemReader.setReader(bufferedReader);
        executionContext = new ExecutionContext();
    }

    @Test
    @DisplayName("Should open file and initialize reader state")
    void open_shouldInitializeBufferedWriter() throws IOException {
        // given
        Path file = tempDir.resolve("test.csv");
        Files.writeString(file,
                """
                id,value,active,ImageUrl
                1,Falcon,true,www.cdn.example.com
                """);
        customItemReader.setResource(new FileSystemResource(file));
        // when
        customItemReader.open(executionContext);
        // then
        assertThat(customItemReader.read()).isNotNull();
    }

    @Test
    @DisplayName("Should skip empty file")
    void open_shouldSkipEmptyFile() throws Exception {
        // given
        Path file = tempDir.resolve("empty.csv");
        Files.createFile(file);
        customItemReader.setResource(new FileSystemResource(file));
        // when
        customItemReader.open(executionContext);
        // then
        assertThat(customItemReader.read()).isNull();
    }

    @Test
    @DisplayName("Should throw exception for invalid file type")
    void open_shouldThrowException_whenInvalidExtension() throws Exception {
        // given
        Path file = tempDir.resolve("invalid.txt");
        Files.writeString(file, "hello");
        // when
        customItemReader.setResource(new FileSystemResource(file));
        // then
        assertThatThrownBy(() ->
                customItemReader.open(executionContext))
                .isInstanceOf(InvalidFileTypeException.class);
    }

    @Test
    @DisplayName("Should read CSV rows correctly")
    void read_shouldReturnEntity() throws Exception {
        // given
        Path file = tempDir.resolve("data.csv");
        Files.writeString(file,
                """
                id,value
                1,Falcon
                2,Starship
                """);
        customItemReader.setResource(new FileSystemResource(file));
        customItemReader.open(executionContext);
        // when
        TestEntity first = (TestEntity) customItemReader.read();
        TestEntity second = (TestEntity) customItemReader.read();
        CsvEntity<?> third = customItemReader.read();
        // then
        assertNotNull(first);
        assertThat(first.getId()).isEqualTo("1");
        assertThat(first.getValue()).isEqualTo("Falcon");

        assertNotNull(second);
        assertThat(second.getId()).isEqualTo("2");
        assertThat(second.getValue()).isEqualTo("Starship");

        assertThat(third).isNull();
    }

    @Test
    @DisplayName("Should return null when iterator is null")
    void read_shouldReturnNull_whenIteratorNull() {
        assertThat(customItemReader.read()).isNull();
    }

    @Test
    @DisplayName("Should close iterator and reader successfully")
    void close_shouldCloseResources() throws Exception {
        // when
        customItemReader.close();
        // then
        verify(iterator).close();
        verify(bufferedReader).close();
    }

    @Test
    @DisplayName("Should close only reader when iterator is null")
    void close_shouldCloseOnlyReader() throws Exception {
        // given
        customItemReader.setIterator(null);
        customItemReader.setReader(bufferedReader);
        // when
        customItemReader.close();
        // then
        verify(bufferedReader).close();
    }

    @Test
    @DisplayName("Should close only iterator when reader is null")
    void close_shouldCloseOnlyIterator() throws Exception {
        // given
        customItemReader.setIterator(iterator);
        customItemReader.setReader(null);
        // when
        customItemReader.close();
        // then
        verify(iterator).close();
    }

    @Test
    @DisplayName("Should throw ItemStreamException when close fails")
    void close_shouldThrowItemStreamException() throws Exception {
        // given
        doThrow(new IOException("close failed"))
                .when(bufferedReader)
                .close();
        customItemReader.setReader(bufferedReader);
        // when / then
        assertThatThrownBy(() -> customItemReader.close())
                .isInstanceOf(ItemStreamException.class)
                .hasMessage("Failed to close CSV reader");
    }
}