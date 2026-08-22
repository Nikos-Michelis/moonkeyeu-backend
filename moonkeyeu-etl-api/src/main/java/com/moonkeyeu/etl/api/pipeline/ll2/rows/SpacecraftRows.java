package com.moonkeyeu.etl.api.pipeline.ll2.rows;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;

import java.time.LocalDate;

/**
 * Rows for {@code spacecraft_type}, {@code spacecraft_status},
 * {@code spacecraft_configuration}, {@code spacecraft} and {@code spacecraft_stage}.
 */
public final class SpacecraftRows {

    /** {@code spacecraft_type}, read from {@code spacecraft_config.type}. */
    public record SpacecraftType(Long id, String name) {

        public static SpacecraftType of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            String name = JsonParser.text(node, "name");
            if (id == null || name == null) {
                return null;
            }
            return new SpacecraftType(id, name);
        }
    }

    /** {@code spacecraft_status}, read from {@code spacecraft.status}. */
    public record SpacecraftStatus(Long id, String name) {

        public static SpacecraftStatus of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            String name = JsonParser.text(node, "name");
            if (id == null || name == null) {
                return null;
            }
            return new SpacecraftStatus(id, name);
        }
    }

    /** {@code spacecraft_configuration}. */
    public record SpacecraftConfiguration(
            Long id,
            String name,
            Long typeId,
            Boolean inUse,
            String capability,
            String history,
            String details,
            LocalDate maidenFlight,
            Double height,
            Double diameter,
            Boolean humanRated,
            Integer crewCapacity,
            Integer payloadCapacity,
            Integer payloadReturnCapacity,
            String flightLife,
            String wikiLink,
            String infoLink,
            Long agencyId
    ) {

        public static SpacecraftConfiguration of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            String name = JsonParser.text(node, "name");
            Long typeId = JsonParser.id(node, "type", "id");
            // name and type_id are both NOT NULL in the schema.
            if (id == null || name == null || typeId == null) {
                return null;
            }
            return new SpacecraftConfiguration(
                    id,
                    name,
                    typeId,
                    JsonParser.bool(node, "in_use"),
                    JsonParser.text(node, "capability"),
                    JsonParser.text(node, "history"),
                    JsonParser.text(node, "details"),
                    JsonParser.date(node, "maiden_flight"),
                    JsonParser.dbl(node, "height"),
                    JsonParser.dbl(node, "diameter"),
                    JsonParser.bool(node, "human_rated"),
                    JsonParser.integer(node, "crew_capacity"),
                    JsonParser.integer(node, "payload_capacity"),
                    JsonParser.integer(node, "payload_return_capacity"),
                    JsonParser.text(node, "flight_life"),
                    JsonParser.text(node, "wiki_link"),
                    JsonParser.text(node, "info_link"),
                    JsonParser.id(node, "agency", "id"));
        }
    }

    /**
     * {@code spacecraft}.
     * <p>
     * {@code description} and {@code flights_count} are NOT NULL in the schema but optional
     * upstream, so they fall back to empty and zero rather than dropping the whole row.
     */
    public record Spacecraft(
            Long id,
            String name,
            String serialNumber,
            Boolean isPlaceholder,
            Boolean inSpace,
            Integer flightsCount,
            Integer missionEndsCount,
            String description,
            Long spacecraftConfId,
            Long statusId
    ) {

        public static Spacecraft of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            String name = JsonParser.text(node, "name");
            if (id == null || name == null) {
                return null;
            }
            Integer flightsCount = JsonParser.integer(node, "flights_count");
            String description = JsonParser.text(node, "description");
            return new Spacecraft(
                    id,
                    name,
                    JsonParser.text(50, node, "serial_number"),
                    JsonParser.bool(node, "is_placeholder"),
                    JsonParser.bool(node, "in_space"),
                    flightsCount == null ? 0 : flightsCount,
                    JsonParser.integer(node, "mission_ends_count"),
                    description == null ? "" : description,
                    JsonParser.id(node, "spacecraft_config", "id"),
                    JsonParser.id(node, "status", "id"));
        }
    }

    /** {@code spacecraft_stage} — one capsule's participation in one launch. */
    public record SpacecraftStage(
            Long id,
            String missionEnd,
            String destination,
            Long spacecraftId,
            Long rocketId,
            Long landingId
    ) {

        public static SpacecraftStage of(JsonNode node, Long rocketId) {
            Long id = JsonParser.id(node, "id");
            if (id == null) {
                return null;
            }
            return new SpacecraftStage(
                    id,
                    JsonParser.text(255, node, "mission_end"),
                    JsonParser.text(255, node, "destination"),
                    JsonParser.id(node, "spacecraft", "id"),
                    rocketId,
                    JsonParser.id(node, "landing", "id"));
        }
    }
}
