package com.moonkeyeu.etl.api.pipeline.ll2.rows;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;

import java.time.LocalDate;

/** Rows for {@code rocket_configuration} and {@code rocket}. */
public final class RocketRows {

    /**
     * {@code rocket_configuration}, read from {@code rocket.configuration}.
     * <p>
     * {@code image_id} points at a {@code rocket_conf_images} row that must be written first —
     * the foreign key runs from the configuration to the image, the opposite direction to every
     * other image table.
     */
    public record RocketConfiguration(
            Long id,
            String name,
            String variant,
            String fullname,
            Boolean active,
            Boolean reusable,
            String description,
            String alias,
            Integer minStage,
            Integer maxStage,
            LocalDate maidenFlight,
            Double length,
            Double diameter,
            Double launchCost,
            Double launchMass,
            Double leoCapacity,
            Double gtoCapacity,
            Double geoCapacity,
            Double ssoCapacity,
            Integer toThrust,
            Integer apogee,
            String infoUrl,
            String wikiUrl,
            Integer totalLaunchCount,
            Integer consecutiveSuccessfulLaunches,
            Integer successfulLaunches,
            Integer failedLaunches,
            Integer pendingLaunches,
            Integer attemptedLandings,
            Integer successfulLandings,
            Integer failedLandings,
            Integer consecutiveSuccessfulLandings,
            Long agencyId,
            Long imageId
    ) {

        public static RocketConfiguration of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            String name = JsonParser.text(node, "name");
            if (id == null || name == null) {
                return null;
            }
            return new RocketConfiguration(
                    id,
                    name,
                    JsonParser.text(node, "variant"),
                    JsonParser.text(node, "full_name"),
                    JsonParser.bool(node, "active"),
                    JsonParser.bool(node, "reusable"),
                    JsonParser.text(node, "description"),
                    JsonParser.text(node, "alias"),
                    JsonParser.integer(node, "min_stage"),
                    JsonParser.integer(node, "max_stage"),
                    JsonParser.date(node, "maiden_flight"),
                    JsonParser.dbl(node, "length"),
                    JsonParser.dbl(node, "diameter"),
                    JsonParser.dbl(node, "launch_cost"),
                    JsonParser.dbl(node, "launch_mass"),
                    JsonParser.dbl(node, "leo_capacity"),
                    JsonParser.dbl(node, "gto_capacity"),
                    JsonParser.dbl(node, "geo_capacity"),
                    JsonParser.dbl(node, "sso_capacity"),
                    JsonParser.integer(node, "to_thrust"),
                    JsonParser.integer(node, "apogee"),
                    JsonParser.text(node, "info_url"),
                    JsonParser.text(node, "wiki_url"),
                    JsonParser.integer(node, "total_launch_count"),
                    JsonParser.integer(node, "consecutive_successful_launches"),
                    JsonParser.integer(node, "successful_launches"),
                    JsonParser.integer(node, "failed_launches"),
                    JsonParser.integer(node, "pending_launches"),
                    JsonParser.integer(node, "attempted_landings"),
                    JsonParser.integer(node, "successful_landings"),
                    JsonParser.integer(node, "failed_landings"),
                    JsonParser.integer(node, "consecutive_successful_landings"),
                    JsonParser.id(node, "manufacturer", "id"),
                    JsonParser.id(node, "image", "id"));
        }
    }

    /** {@code rocket} — little more than a join between a launch and a configuration. */
    public record Rocket(Long id, Long rocketConfId) {

        public static Rocket of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            if (id == null) {
                return null;
            }
            return new Rocket(id, JsonParser.id(node, "configuration", "id"));
        }
    }
}