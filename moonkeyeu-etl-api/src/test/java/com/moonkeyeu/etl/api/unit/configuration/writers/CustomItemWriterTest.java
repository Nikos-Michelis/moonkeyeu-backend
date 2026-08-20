package com.moonkeyeu.etl.api.unit.configuration.writers;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.moonkeyeu.etl.api.config.TestEntity;
import com.moonkeyeu.etl.api.configuration.batch.writers.CustomItemWriter;
import com.moonkeyeu.etl.api.model.CsvEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomItemWriterTest {

    @Mock
    private Resource resource;
    @Mock
    private CsvMapper csvMapper;
    @InjectMocks
    @Spy
    private CustomItemWriter customItemWriter;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {

        File tempFile = tempDir.resolve("test.csv").toFile();

        when(resource.getFile()).thenReturn(tempFile);

        customItemWriter.setResource(resource);
    }

    @Test
    @DisplayName("Should open file and initialize writer state")
    void open_shouldInitializeBufferedWriter() {
        // given
        ExecutionContext executionContext = new ExecutionContext();
        // when
        customItemWriter.open(executionContext);
        // then
        assertThat(customItemWriter.isHeaderWritten()).isFalse();
        assertThat(customItemWriter.getBufferedWriter()).isNotNull();
    }

    @Test
    @DisplayName("Should initialize headers and write CSV object content")
    void write_shouldInitializeHeadersAndWriteObjectContent() throws Exception {

        // given
        ExecutionContext context = new ExecutionContext();
        customItemWriter.open(context);
        TestEntity entity = new TestEntity("1", "Falcon");
        Chunk<Object> chunk = new Chunk<>(List.of(entity));

        // when
        customItemWriter.write(chunk);
        // then
        assertThat(customItemWriter.isHeaderWritten()).isTrue();
        assertThat(customItemWriter.getWriter()).isNotNull();
        customItemWriter.close();
        String content = Files.readString(resource.getFile().toPath());
        assertThat(content)
                .contains("id,value");
        assertThat(content)
                .contains("1,Falcon");
    }

    @Test
    @DisplayName("Should skip writing when chunk is empty")
    void write_shouldSkipEmptyChunk() {
        // given
        ExecutionContext context = new ExecutionContext();
        customItemWriter.open(context);
        Chunk<CsvEntity<?>> chunk = new Chunk<>(List.of());
        // when
        customItemWriter.write(chunk);
        // then
        assertNull(customItemWriter.getWriter());
        assertNotNull(customItemWriter.getBufferedWriter());
        verify(customItemWriter, times(0)).close();
    }

    @Test
    @DisplayName("Should initialize headers and write CSV collection content")
    void write_shouldInitializeHeadersAndWriteCollectionContent() throws Exception {

        // given
        ExecutionContext context = new ExecutionContext();
        customItemWriter.open(context);
        Chunk<Object> chunk = new Chunk<>(List.of(
                new TestEntity("1", "Falcon", true),
                new TestEntity("2", "Falcon", true),
                new TestEntity("3", "Falcon", true)
        ));

        // when
        customItemWriter.write(chunk);
        // then
        assertThat(customItemWriter.isHeaderWritten()).isTrue();
        assertThat(customItemWriter.getWriter()).isNotNull();
        customItemWriter.close();
        String content = Files.readString(resource.getFile().toPath());
        
        assertThat(content)
                .contains("id,value,active");
        assertThat(content)
                .contains("1,Falcon,true")
                .contains("2,Falcon,true")
                .contains("3,Falcon,true");
    }

    @Test
    @DisplayName("Should write chunk and generate headers on first call")
    void write_shouldWriteDataAndGenerateHeader() {
        // given
        ExecutionContext executionContext = new ExecutionContext();
        customItemWriter.open(executionContext);
        TestEntity item = new TestEntity("A", "test");
        Chunk<Object> chunk = new Chunk<>(List.of(item));
        // when
        customItemWriter.write(chunk);
        // then
        assertThat(customItemWriter.isHeaderWritten()).isTrue();
    }

    @Test
    @DisplayName("Should close writer without exception")
    void close_shouldCloseResources() {
        // when
        customItemWriter.open(new ExecutionContext());
        // then
        assertNotNull(customItemWriter);
        assertDoesNotThrow(() -> customItemWriter.close());
        assertDoesNotThrow(() -> customItemWriter.getBufferedWriter().close());
    }
}