package com.moonkeyeu.etl.api.configuration.batch.readers;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidFileTypeException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemStream;
import org.springframework.batch.infrastructure.item.ItemStreamException;
import org.springframework.batch.infrastructure.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Objects;

@Setter
@Getter
@Slf4j
public class JsonItemReader implements ResourceAwareItemReaderItemStream<JsonNode>, ItemStream {
    private ObjectMapper mapper;
    private Resource resource;
    private JsonParser parser;
    private boolean inResultsArray = false;

    public JsonItemReader(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }
    /**
     * Reads the result array nodes one by one and extract their objects
     **/
    @Override
    public JsonNode read() throws Exception {
        if (parser == null) {
            return null;
        }

        while (parser.nextToken() != null) {
            /*
             * If the parser is currently at the start of a JSON object inside a "results" array,
             * read the entire object into a JsonNode and return it.
             * This allows streaming one object at a time without loading the whole array into memory.
             */
            if (inResultsArray && parser.currentToken() == JsonToken.START_OBJECT) {
                return mapper.readTree(parser);
            }
            /*
             * If the parser is currently at the field name "results",
             * move the parser to the start of the array (the value of "results")
             * and set inResultsArray = true so that subsequent objects can be read one by one (it reads the nest result array node).
             */
            if (parser.currentToken() == JsonToken.PROPERTY_NAME && "results".equals(parser.currentName())) {
                parser.nextToken();
                inResultsArray = true;
            }
            /*
             * Marks the end of the "results" array by setting inResultsArray to false,
             * so the reader knows there are no more any objects to stream in this array.
             */
            if (parser.currentToken() == JsonToken.END_ARRAY && inResultsArray) {
                inResultsArray = false;
            }
        }

        return null;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            if (!resource.isReadable()) {
                log.warn("File not found: {}", this.resource);
                return;
            }

            if (!resource.isFile() || !Objects.requireNonNull(resource.getFilename()).endsWith(".json")) {
                throw new InvalidFileTypeException("Invalid file type: " + resource.getFilename());
            }

            parser = mapper.createParser(resource.getInputStream());
            /**
             * parse the next field until finds the all_results field
             **/
            while (parser.nextToken() != null) {
                if (parser.currentToken() == JsonToken.PROPERTY_NAME && "all_results".equals(parser.currentName())) {
                    parser.nextToken();
                    break;
                }
            }

        } catch (IOException e) {
            throw new ItemStreamException("Failed to open JSON file", e);
        }
    }

    @Override
    public void close() throws ItemStreamException {
        if (parser != null) {
            parser.close();
        }
    }
}
