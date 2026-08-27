package com.moonkeyeu.etl.api.configuration.batch.writers;

import com.moonkeyeu.etl.api.pipeline.ll2.Table;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A table's write statement, built from its column list.
 * <p>
 * This is the boundary that replaces the old implicit contract. Previously a column was populated
 * if — and only if — a DTO field name, a CSV header and a Jackson annotation on an entity all
 * happened to agree on the same string, with nothing checking that they did. Here the column name
 * sits next to a method reference on the record, so a renamed component is a compile error and a
 * renamed column is a SQL error on the first batch.
 *
 * @param <T> the row record this writes
 */
public final class Upsert<T> {

    /**
     * Alias for the row being inserted, used by the ON DUPLICATE KEY UPDATE clause.
     */
    private static final String NEW_ROW = "src";

    /** How a conflicting row is handled. */
    public enum Mode {
        /**
         * Overwrite non-key columns, but never with a null. Rows for the same entity arrive from
         * several paths at different levels of detail — an agency via a launch carries a fraction
         * of what the agencies feed returns — so a sparse projection must not blank out a full one.
         */
        MERGE,
        /**
         * Leave the existing row alone. For reference data that upstream does not revise
         * (countries, statuses, orbits), skipping to write is cheaper and safer than rewriting
         * identical values on every run.
         */
        KEEP_EXISTING
    }

    /**
     * @param updatable false for a column that is written when the row is first inserted but never
     *                  overwritten afterwards. Image URLs are the case that matters: the media step
     *                  rewrites them to point at S3 or local storage once the file has been
     *                  downloaded, and the next ETL run must not merge the upstream URL back over
     *                  that, which would re-download every image on every run.
     */
    private record Column<T>(String name, Function<T, Object> getter, boolean updatable) {}

    private final Table table;
    private final List<Column<T>> columns;
    private final String sql;

    private Upsert(Table table, List<Column<T>> columns, Mode mode) {
        this.table = table;
        this.columns = List.copyOf(columns);
        this.sql = buildSql(table, this.columns, mode);
    }

    public static <T> Builder<T> into(Table table) {
        return new Builder<>(table);
    }

    public Table table() {
        return table;
    }

    public String sql() {
        return sql;
    }

    /** Binds one row's values in column order. */
    public void bind(PreparedStatement statement, T row) throws SQLException {
        for (int i = 0; i < columns.size(); i++) {
            statement.setObject(i + 1, columns.get(i).getter().apply(row));
        }
    }

    private static <T> String buildSql(Table table, List<Column<T>> columns, Mode mode) {
        String columnList = columns.stream().map(Column::name).collect(Collectors.joining(", "));
        String placeholders = columns.stream().map(c -> "?").collect(Collectors.joining(", "));

        if (mode == Mode.KEEP_EXISTING) {
            return "INSERT IGNORE INTO " + table.tableName()
                    + " (" + columnList + ") VALUES (" + placeholders + ")";
        }

        // The first column is the primary key and is never updated, nor are insert-only columns.
        // Both sides are qualified. The row alias puts a second set of identically named columns
        // into scope, so a bare column reference inside the COALESCE is ambiguous and MySQL rejects
        // the statement with "Column 'x' in field list is ambiguous". The table name refers to the
        // stored row, the alias to the row being inserted.
        String tableName = table.tableName();
        String assignments = columns.stream()
                .skip(1) // skip pk column
                .filter(Column::updatable)
                .map(c -> tableName + "." + c.name()
                        + " = COALESCE(" + NEW_ROW + "." + c.name() + ", " + tableName + "." + c.name() + ")")
                .collect(Collectors.joining(",\n    "));
        // empty only for the reference tables because are insert only
        if (assignments.isEmpty()) {
            return "INSERT IGNORE INTO " + tableName
                    + " (" + columnList + ") VALUES (" + placeholders + ")";
        }

        // Row-alias form; requires MySQL 8.0.19 or later (the project runs 8.0.30).
        return "INSERT INTO " + tableName
                + " (" + columnList + ") VALUES (" + placeholders + ") AS " + NEW_ROW
                + "\nON DUPLICATE KEY UPDATE\n    " + assignments;
    }

    /** Fluent column list. The first column added must be the primary key. */
    public static final class Builder<T> {

        private final Table table;
        private final List<Column<T>> columns = new ArrayList<>();

        private Builder(Table table) {
            this.table = table;
        }

        public Builder<T> col(String name, Function<T, Object> getter) {
            columns.add(new Column<>(name, getter, true));
            return this;
        }

        /** A column set on insert and never overwritten again. See {@link Column#updatable()}. */
        public Builder<T> insertOnly(String name, Function<T, Object> getter) {
            columns.add(new Column<>(name, getter, false));
            return this;
        }

        public Upsert<T> merge() {
            return new Upsert<>(table, columns, Mode.MERGE);
        }

        public Upsert<T> keepExisting() {
            return new Upsert<>(table, columns, Mode.KEEP_EXISTING);
        }
    }
}
