package com.moonkeyeu.etl.api.configuration.batch.writers;

import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.moonkeyeu.etl.api.configuration.files.FilePathProvider;
import com.moonkeyeu.etl.api.configuration.mappers.JacksonConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.*;
import org.springframework.core.io.Resource;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;


@Slf4j
@Setter
@Getter
public class CustomItemWriter implements ItemWriter<Object>, ItemStream {
    private CsvMapper csvMapper;
    private Resource resource;
    private Class<?> type;
    private BufferedWriter bufferedWriter;
    private SequenceWriter writer;
    private FilePathProvider filePathProvider;
    private Chunk<?> data;
    private boolean headerWritten = false;

    public CustomItemWriter(CsvMapper csvMapper) {
        this.csvMapper = csvMapper;
    }

    @Override
    public void write(Chunk<?> chunk) {
        try {
            if (chunk.isEmpty()) {
                return;
            }

            if (!isHeaderWritten()) {
                String[] headers = getHeaders(chunk.getItems().getFirst());
                CsvSchema csvSchema = buildSchema(headers);
                writer = buildSequenceWriter(csvSchema);
                setHeaderWritten(true);
            }

            for (Object obj : chunk.getItems()) {
                writer.write(flattenObject(obj));
            }
        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException("Failed to write chunk", e);
        }
    }

    @Override
    public void open(ExecutionContext executionContext) {
        try {
            this.headerWritten = false;
            bufferedWriter = Files.newBufferedWriter(resource.getFile().toPath(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        } catch (IOException e) {
            throw new ItemStreamException(e);
        }
    }

    @Override
    public void close() throws ItemStreamException {
        try {
            if (bufferedWriter != null) bufferedWriter.close();
            if (writer != null) writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close file " + resource.getFilename(), e);
        }
    }

    private SequenceWriter buildSequenceWriter(CsvSchema csvSchema) throws IOException, IllegalAccessException {
        return csvMapper.writer(csvSchema).writeValues(bufferedWriter);
    }

    private CsvSchema buildSchema(String[] headers) {
        CsvSchema.Builder builder = CsvSchema.builder();
        Arrays.stream(headers).forEach(builder::addColumn);
        return builder.setUseHeader(true).build();
    }

    private String[] getHeaders(Object obj) {
        Set<String> headerSet = new LinkedHashSet<>();
        if (obj != null) headerSet.addAll(flattenAndExtractHeaders(obj.getClass()));
        return headerSet.toArray(new String[0]);
    }

    /**
     * @param obj We should use object instance instead of clazz type in order to extract the actual values
     * @return A key value pair
     * @throws IllegalAccessException If this Field object is enforcing Java language access control, and the underlying field is inaccessible, the method throws an IllegalAccessExceptio
     */
    private Map<String, Object> flattenObject(Object obj) throws IllegalAccessException {
        Map<String, Object> fieldMap = new LinkedHashMap<>();

        if (obj == null) return fieldMap;

        for (Field field : obj.getClass().getDeclaredFields()) {

            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()) || field.isSynthetic()) {
                continue;
            }

            field.setAccessible(true);
            Object value = field.get(obj);
            if (isPrimitiveOrString(field.getType())) {
                fieldMap.put(field.getName(), value);
            } else {
                fieldMap.putAll(flattenObject(value));
            }
        }
        return fieldMap;
    }

    /**
     * @param clazz use clazz instead of object instance in order to get the class structure of the code
     * @return A set of headers namely the class attribute names
     */
    private Set<String> flattenAndExtractHeaders(Class<?> clazz) {
        Set<String> headers = new LinkedHashSet<>();
        for (Field field : clazz.getDeclaredFields()) {

            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()) || field.isSynthetic()) {
                continue;
            }

            field.setAccessible(true);
            if (isPrimitiveOrString(field.getType())) {
                headers.add(field.getName());
            } else {
                headers.addAll(flattenAndExtractHeaders(field.getType()));
            }
        }
        return headers;
    }


    private boolean isPrimitiveOrString(Class<?> type) {
        return type.isPrimitive()
                || type.equals(String.class)
                || Number.class.isAssignableFrom(type)
                || Boolean.class.equals(type)
                || Character.class.equals(type)
                || Date.class.isAssignableFrom(type);
    }
}
