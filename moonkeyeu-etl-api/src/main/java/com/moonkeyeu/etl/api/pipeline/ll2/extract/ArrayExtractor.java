package com.moonkeyeu.etl.api.pipeline.ll2.extract;

import com.fasterxml.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.ll2.Context;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.RowSink;
import com.moonkeyeu.etl.api.pipeline.ll2.Table;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Emits one row per element of an array, where each element needs a foreign key inherited from an
 * ancestor.
 *
 * @param path    field names to walk from the node handed to {@link #extract}
 * @param table   where the rows land
 * @param factory builds a row from one array element plus the inherited context
 * @param pk      reads the row's primary key, for deduplication
 */
public record ArrayExtractor<T>(
        String[] path,
        Table table,
        BiFunction<JsonNode, Context, T> factory,
        Function<T, Object> pk
) implements Extractor {

    @Override
    public void extract(JsonNode node, Context context, RowSink sink) {
        for (JsonNode element : JsonParser.array(node, path)) {
            T row = factory.apply(element, context);
            if (row != null) {
                sink.emit(table, pk.apply(row), row);
            }
        }
    }

    /** Reads better at the registration site than a bare array literal. */
    public static String[] path(String... segments) {
        return segments;
    }
}
