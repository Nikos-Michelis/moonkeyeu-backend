package com.moonkeyeu.etl.api.pipeline.core;

import com.moonkeyeu.etl.api.pipeline.ll2.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects the rows extracted from one or more JSON records, keyed by target table.
 * Deduplication is the point, not a side effect. The same country appears in hundreds of launches
 * and the same agency in thousands; Keying by primary key here means a chunk of 150
 * launches writes each country once.
 */
public final class RowSink {

    private final Map<Table, LinkedHashMap<Object, Object>> rows = new EnumMap<>(Table.class);

    /**
     * Records a row. Null rows and rows with a null primary key are dropped — upstream (Source of direction) regularly
     * omits whole sub-objects, and a row we cannot key is a row we cannot upsert.
     * <p>
     * First write wins. Within a chunk the same entity may be reached by several paths (an agency
     * via {@code launch_service_provider} and again via {@code mission.agencies}); the first is
     * kept rather than the last so that a richer projection encountered early is not replaced by a
     * sparser one encountered later.
     */
    public void emit(Table table, Object primaryKey, Object row) {
        if (primaryKey == null || row == null) {
            return;
        }
        // computeIfAbsent(key, Function -> enter into new map)
        rows.computeIfAbsent(table, t -> new LinkedHashMap<>()).putIfAbsent(primaryKey, row);
    }

    public <T> List<T> rows(Table table) {
        LinkedHashMap<Object, Object> key = rows.get(table);
        if (key == null || key.isEmpty()) {
            return List.of();
        }
        return (List<T>) List.copyOf(key.values());
    }

    public boolean isEmpty() {
        return rows.values().stream().allMatch(Map::isEmpty);
    }

    /** Total rows across all tables. Used for logging. */
    public int size() {
        return rows.values().stream().mapToInt(Map::size).sum();
    }

    /**
     * Folds the sinks produced for each JSON record in a chunk into one, so the writer issues a
     * single batch per table for the whole chunk instead of one per record.
     * <p>
     * The same entity often shows up in several records, so duplicate keys are expected. The first
     * row seen for a key wins; later ones are dropped. Insertion order is kept.
     * <p>
     * Two launches by the same provider:
     * <pre>
     * record 1:  AGENCIES {121: SpaceX}   LAUNCH {8001: Starlink G7-1}
     * record 2:  AGENCIES {121: SpaceX}   LAUNCH {8002: Starlink G7-2}
     * merged:    AGENCIES {121: SpaceX}   LAUNCH {8001: ..., 8002: ...}
     * </pre>
     * Agency 121 appeared twice and is written once; the two launches have distinct keys and both
     * survive. Four statements become two batches.
     */
    public static RowSink merge(Collection<? extends RowSink> sinks) {
        RowSink merged = new RowSink();
        for (RowSink sink : sinks) {
            sink.rows.forEach((table, key) -> {
                LinkedHashMap<Object, Object> target = merged.rows.computeIfAbsent(table, t -> new LinkedHashMap<>());
                key.forEach(target::putIfAbsent);
            });
        }
        return merged;
    }

    /** Tables that actually received rows, in load order. Empty tables are skipped by the writer. */
    public List<Table> populatedTables() {
        List<Table> populated = new ArrayList<>();
        for (Table table : Table.inLoadOrder()) {
            LinkedHashMap<Object, Object> key = rows.get(table);
            if (key != null && !key.isEmpty()) {
                populated.add(table);
            }
        }
        return populated;
    }
}