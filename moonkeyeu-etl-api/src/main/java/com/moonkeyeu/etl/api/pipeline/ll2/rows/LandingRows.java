package com.moonkeyeu.etl.api.pipeline.ll2.rows;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;

/**
 * Rows for {@code landing_type}, {@code landing_zone} and {@code landing}.
 * <p>
 * Both a booster ({@code launcher_stage}) and a capsule ({@code spacecraft_stage}) carry a landing
 * of exactly this shape, so one set of records serves both branches of the tree.
 */
public final class LandingRows {

    /** {@code landing_type}, read from {@code landing.type}. */
    public record LandingType(Long id, String name, String abbrev, String description) {

        public static LandingType of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            if (id == null) {
                return null;
            }
            return new LandingType(
                    id,
                    JsonParser.text(node, "name"),
                    JsonParser.text(45, node, "abbrev"),
                    JsonParser.text(node, "description"));
        }
    }

    /** {@code landing_zone}, read from {@code landing.landing_location}. */
    public record LandingZone(
            Long id,
            String name,
            String abbrev,
            String description,
            Integer successfulLandings,
            Long locationId
    ) {

        public static LandingZone of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            if (id == null) {
                return null;
            }
            return new LandingZone(
                    id,
                    JsonParser.text(node, "name"),
                    JsonParser.text(45, node, "abbrev"),
                    JsonParser.text(node, "description"),
                    JsonParser.integer(node, "successful_landings"),
                    JsonParser.id(node, "location", "id"));
        }
    }

    /**
     * {@code landing}.
     * <p>
     * {@code success} is a VARCHAR column but upstream sends a boolean; it is stored as its text
     * form, which is what the previous pipeline did once the value had passed through CSV.
     */
    public record Landing(
            Long id,
            Boolean attempt,
            String success,
            String description,
            Integer downrangeDistance,
            Long landingZoneId,
            Long landingTypeId
    ) {

        public static Landing of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            if (id == null) {
                return null;
            }
            return new Landing(
                    id,
                    JsonParser.bool(node, "attempt"),
                    JsonParser.text(45, node, "success"),
                    JsonParser.text(node, "description"),
                    JsonParser.integer(node, "downrange_distance"),
                    JsonParser.id(node, "landing_location", "id"),
                    JsonParser.id(node, "type", "id"));
        }
    }
}
