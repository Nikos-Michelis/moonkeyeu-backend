package com.moonkeyeu.etl.api.configuration.batch.writers;

import com.moonkeyeu.etl.api.configuration.files.CsvSource;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemStream;

import java.util.EnumMap;
import java.util.Map;

public class ItemWriterRegistry implements ItemStream {
    private final Map<CsvSource, CustomItemWriter> writers = new EnumMap<>(CsvSource.class);

    public void register(CsvSource source, CustomItemWriter writer) {
        writers.put(source, writer);
    }

    public CustomItemWriter get(CsvSource source) {
        return writers.get(source);
    }

    @Override
    public void open(ExecutionContext executionContext) {
        writers.values().forEach(writer -> writer.open(executionContext));
    }

    @Override
    public void update(ExecutionContext executionContext) {
        writers.values().forEach(writer -> writer.update(executionContext));
    }

    @Override
    public void close() {
        writers.values().forEach(ItemStream::close);
    }
}
