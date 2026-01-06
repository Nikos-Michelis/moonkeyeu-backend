package com.moonkeyeu.etl.api.configuration.batch.readers;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.moonkeyeu.etl.api.configuration.mappers.MappersConfig;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidFileTypeException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.*;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@Setter
public class CustomItemReader implements ItemReader<CsvEntity<?>>, ItemStream {
     private CsvMapper csvMapper;
     private Resource resource;
     private Class<?> type;
     private MappingIterator<CsvEntity<?>> iterator;
     private BufferedReader reader;

     public CustomItemReader() {
         this.csvMapper = new MappersConfig().csvMapper();
     }

     @Override
     public void open(ExecutionContext executionContext) throws ItemStreamException {
         try {
             if (!resource.exists() || resource.getFile().length() == 0) {
                 log.warn("File not found: {}", this.resource);
                 return;
             }

             if (!resource.isFile() || !Objects.requireNonNull(resource.getFilename()).endsWith(".csv")) {
                 throw new InvalidFileTypeException("Invalid file type: " + resource.getFilename());
             }

              this.reader = new BufferedReader(
                      new InputStreamReader(new FileInputStream(this.resource.getFile()), StandardCharsets.UTF_8));

             CsvSchema schema = CsvSchema.emptySchema().withHeader();
             this.iterator = csvMapper.readerFor(type)
                     .with(schema)
                     .readValues(reader);

         } catch (IOException e) {
             log.error("Failed to open file {}: {}", this.resource.getFilename(), e.getMessage(), e);
             throw new ItemStreamException("Failed to open file: " + this.resource.getFilename(), e);
         }
     }

     @Override
     public CsvEntity<?> read() {
         try {
             if (iterator != null && iterator.hasNext()) return iterator.next();
             return null;
         } catch (Exception e) {
             log.error("Failed to read CSV row in file {}: {}", resource.getFilename(), e.getMessage(), e);
             throw new NonTransientResourceException("CSV read error in file " + resource.getFilename(), e);
         }
     }

    @Override
    public void close() throws ItemStreamException {
        try {
            if (reader != null) {
                log.info("closing buffer reader {}", reader.getClass());
                reader.close();
            }
        } catch (IOException e) {
            throw new ItemStreamException("Failed to close CSV reader", e);
        }
    }
}

