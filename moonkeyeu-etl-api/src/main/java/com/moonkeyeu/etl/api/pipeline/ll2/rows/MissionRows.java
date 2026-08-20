package com.moonkeyeu.etl.api.pipeline.ll2.rows;

import com.fasterxml.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.CompositeKey;

/** Rows for {@code orbit}, {@code mission} and {@code mission_has_agencies}. */
public final class MissionRows {

    /** {@code orbit}, read from {@code mission.orbit}. */
    public record Orbit(Long id, String name, String abbrev) {

        public static Orbit of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            String name = JsonParser.text(node, "name");
            String abbrev = JsonParser.text(45, node, "abbrev");
            // Both columns are NOT NULL in the schema.
            if (id == null || name == null || abbrev == null) {
                return null;
            }
            return new Orbit(id, name, abbrev);
        }
    }

    /** {@code mission}. */
    public record Mission(
            Long id,
            String name,
            String description,
            String type,
            Long orbitId
    ) {

        public static Mission of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            String name = JsonParser.text(node, "name");
            if (id == null || name == null) {
                return null;
            }
            return new Mission(
                    id,
                    name,
                    JsonParser.text(node, "description"),
                    JsonParser.text(node, "type"),
                    JsonParser.id(node, "orbit", "id"));
        }
    }

    /** {@code mission_has_agencies}. */
    public record MissionHasAgencies(Long id, Long missionId, Long agencyId) {

        public static MissionHasAgencies of(Long missionId, Long agencyId) {
            // Historical key order: agency first, then mission.
            Long id = CompositeKey.from(agencyId, missionId, Long::parseLong);
            if (id == null) {
                return null;
            }
            return new MissionHasAgencies(id, missionId, agencyId);
        }
    }
}
