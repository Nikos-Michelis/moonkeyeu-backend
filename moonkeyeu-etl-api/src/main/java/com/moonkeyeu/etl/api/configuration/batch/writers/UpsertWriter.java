package com.moonkeyeu.etl.api.configuration.batch.writers;

import com.moonkeyeu.etl.api.pipeline.core.RowSink;
import com.moonkeyeu.etl.api.pipeline.ll2.Table;
import com.moonkeyeu.etl.api.pipeline.ll2.registry.UpsertRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpsertWriter implements ItemWriter<RowSink> {

    /**
     * Rows per round trip. With {@code rewriteBatchedStatements=true} the driver folds each batch
     * into a single multi-row INSERT, so this is the number of rows per statement, not per call.
     */
    private static final int BATCH_SIZE = 1000;

    private final JdbcTemplate jdbcTemplate;
    private final UpsertRegistry registry;

    @Override
    public void write(Chunk<? extends RowSink> chunk) {
        if (chunk.isEmpty()) {
            return;
        }

        RowSink merged = RowSink.merge(chunk.getItems());
        if (merged.isEmpty()) {
            return;
        }

        int written = 0;
        for (Table table : merged.populatedTables()) {
            List<Object> rows = merged.rows(table);
            Upsert<Object> upsert = registry.get(table);
            jdbcTemplate.batchUpdate(upsert.sql(), rows, BATCH_SIZE, upsert::bind);
            written += rows.size();
        }

        log.debug("Wrote {} rows across {} tables from {} source records", written, merged.populatedTables().size(), chunk.size());
    }
}
