package com.moonkeyeu.etl.api.configuration.batch.processors;

import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.settings.exceptions.DataCleaningException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.item.ItemProcessor;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

@Slf4j
public class CleaningProcessor implements ItemProcessor<CsvEntity<?>, CsvEntity<?>> {
    private static final String UNKNOWN_VALUE = "Unknown";

    @Override
    public CsvEntity<?> process(@NonNull CsvEntity<?> item) {

        if (item.getPrimaryKey() == null) return null;

        for (Field field : item.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object original = field.get(item);
                Object cleaned = handleNullEmptyValues(original);
                cleaned = handleSpecialCharacters(cleaned);
                field.set(item, cleaned);
            } catch (IllegalAccessException e) {
                throw new DataCleaningException("Error cleaning field " + field.getName() + " for entity " + item.getClass().getSimpleName());
            }
        }
        return item;
    }

    private Object handleNullEmptyValues(Object value) {
        if (value instanceof String str) {
            str = str.trim();
            return str.isEmpty() ? null : str;
        }
        return value;
    }

    private Object handleSpecialCharacters(Object value) {
        if (value == null) return null;
        Pattern pattern = Pattern.compile("\\?{2,}");
        return pattern.matcher(value.toString()).matches() ? UNKNOWN_VALUE : value;
    }
}