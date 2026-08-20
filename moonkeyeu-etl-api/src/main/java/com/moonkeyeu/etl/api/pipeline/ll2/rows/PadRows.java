package com.moonkeyeu.etl.api.pipeline.ll2.rows;

import com.fasterxml.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.CompositeKey;

/** Rows for {@code location}, {@code launch_pad} and {@code pad_has_agencies}. */
public final class PadRows {

    /**
     * {@code location}. Reached from two places — a launch pad and a booster's landing zone — so
     * the same location id can arrive twice within one chunk and is deduplicated by RowSink.
     */
    public record Location(
            Long id,
            String name,
            String description,
            String mapImage,
            String locationTimezone,
            Integer totalLaunchCount,
            Integer totalLandingCount
    ) {

        public static Location of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            if (id == null) {
                return null;
            }
            return new Location(
                    id,
                    JsonParser.text(node, "name"),
                    JsonParser.text(node, "description"),
                    JsonParser.text(node, "map_image"),
                    JsonParser.text(45, node, "timezone_name"),
                    JsonParser.integer(node, "total_launch_count"),
                    JsonParser.integer(node, "total_landing_count"));
        }
    }

    /**
     * {@code launch_pad}.
     * <p>
     * {@code agency_id} is the launch's service provider, not a property of the pad itself —
     * upstream does not attach an agency to a pad, and the previous pipeline picked this up
     * implicitly because both shared the column name {@code agency_id} in the flattened CSV row.
     * That behaviour is preserved, but now it is passed in explicitly instead of happening by
     * name collision.
     */
    public record LaunchPad(
            Long id,
            String name,
            Boolean active,
            String description,
            String infoUrl,
            String wikiUrl,
            String mapUrl,
            Double latitude,
            Double longitude,
            String mapImage,
            Integer totalLaunchCount,
            Integer orbitalLaunchAttemptCount,
            Long locationId
    ) {

        public static LaunchPad of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            Long locationId = JsonParser.id(node, "location", "id");

            if (id == null || locationId == null) {
                return null;
            }

            return new LaunchPad(
                    id,
                    JsonParser.text(node, "name"),
                    JsonParser.bool(node, "active"),
                    JsonParser.text(node, "description"),
                    JsonParser.text(node, "info_url"),
                    JsonParser.text(node, "wiki_url"),
                    JsonParser.text(node, "map_url"),
                    JsonParser.dbl(node, "latitude"),
                    JsonParser.dbl(node, "longitude"),
                    JsonParser.text(node, "map_image"),
                    JsonParser.integer(node, "total_launch_count"),
                    JsonParser.integer(node, "orbital_launch_attempt_count"),
                    locationId);
        }
    }

    /** {@code pad_has_agencies} — which providers have flown from this pad. */
    public record PadHasAgencies(Long id, Long launchPadId, Long agencyId) {

        public static PadHasAgencies of(Long launchPadId, Long agencyId) {
            Long id = CompositeKey.from(agencyId, launchPadId, Long::parseLong);
            if (id == null) {
                return null;
            }
            return new PadHasAgencies(id, launchPadId, agencyId);
        }
    }
}
