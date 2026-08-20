package com.moonkeyeu.etl.api.pipeline.ll2.rows;

import com.fasterxml.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.CompositeKey;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Rows for {@code program_type}, {@code programs}, {@code programs_has_agencies} and
 * {@code launch_has_programs}.
 */
public final class ProgramRows {

    /** {@code program_type}, read from {@code program.type}. */
    public record ProgramType(Long id, String name) {

        public static ProgramType of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            String name = JsonParser.text(node, "name");
            if (id == null || name == null) {
                return null;
            }
            return new ProgramType(id, name);
        }
    }

    /**
     * {@code programs}.
     * <p>
     * {@code start_date} is a DATE column but upstream sends a full timestamp; {@code Json.date}
     * accepts both and truncates.
     */
    public record Program(
            Long id,
            String name,
            String infoUrl,
            String wikiUrl,
            String description,
            Instant startDate,
            Long typeId
    ) {

        public static Program of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            String name = JsonParser.text(node, "name");
            Long typeId = JsonParser.id(node, "type", "id");
            if (id == null) {
                return null;
            }
            return new Program(
                    id,
                    name,
                    JsonParser.text(node, "info_url"),
                    JsonParser.text(node, "wiki_url"),
                    JsonParser.text(node, "description"),
                    JsonParser.instant(node, "start_date"),
                    typeId);
        }
    }

    /** {@code programs_has_agencies}. */
    public record ProgramHasAgencies(Long id, Long programId, Long agencyId) {

        public static ProgramHasAgencies of(Long programId, Long agencyId) {
            Long id = CompositeKey.from(programId, agencyId, Long::parseLong);
            if (id == null) {
                return null;
            }
            return new ProgramHasAgencies(id, programId, agencyId);
        }
    }

    /** {@code launch_has_programs}. */
    public record LaunchHasProgram(String id, Long programId, String launchId) {

        public static LaunchHasProgram of(String launchId, Long programId) {
            String id = CompositeKey.from(launchId, programId, String::valueOf);
            if (id == null) {
                return null;
            }
            return new LaunchHasProgram(id, programId, launchId);
        }
    }
}
