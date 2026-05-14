package com.moonkeyeu.etl.api.unit.configuration.processors;

import com.moonkeyeu.etl.api.config.TestEntity;
import com.moonkeyeu.etl.api.configuration.batch.processors.CleaningProcessor;
import com.moonkeyeu.etl.api.model.CsvEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CleaningProcessorTest {
    @InjectMocks
    private CleaningProcessor cleaningProcessor;

    @Test
    @DisplayName("Should return null when primary key is null")
    void shouldReturnNull_whenPrimaryKeyIsNull() {

        TestEntity entity = new TestEntity(null, "value");

        CsvEntity<?> result = cleaningProcessor.process(entity);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should clean string fields correctly")
    void shouldCleanStringFields() {

        TestEntity entity = new TestEntity("123", "  hello  ");

        CsvEntity<?> result = cleaningProcessor.process(entity);

        assertNotNull(result);
        assertThat(((TestEntity) result).getValue())
                .isEqualTo("hello");
    }

    @Test
    @DisplayName("Should convert empty string to null")
    void shouldConvertEmptyStringToNull() {

        TestEntity entity = new TestEntity("123", "   ");

        CsvEntity<?> result = cleaningProcessor.process(entity);

        assertNotNull(result);
        assertThat(((TestEntity) result).getValue())
                .isNull();
    }

    @Test
    @DisplayName("Should replace ??? with Unknown")
    void shouldReplaceQuestionMarksWithUnknown() {

        TestEntity entity = new TestEntity("123", "????");

        CsvEntity<?> result = cleaningProcessor.process(entity);

        assertNotNull(result);
        assertThat(((TestEntity) result).getValue())
                .isEqualTo("Unknown");
    }

    @Test
    @DisplayName("Should keep normal values unchanged")
    void shouldKeepNormalValues() {

        TestEntity entity = new TestEntity("123", "Hello World");

        CsvEntity<?> result = cleaningProcessor.process(entity);

        assertNotNull(result);
        assertThat(((TestEntity) result).getValue())
                .isEqualTo("Hello World");
    }
}