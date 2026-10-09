package com.moonkeyeu.etl.api.pipeline.ll2.rows;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.CompositeKey;

import java.time.Instant;

/**
 * Rows for the launch itself and the media attached to it: {@code launch_status},
 * {@code net_precision}, {@code launch}, {@code videos}, {@code updates}, {@code info_urls} and
 * {@code mission_patches}.
 */
public final class LaunchRows {

    /** {@code launch_status}. */
    public record LaunchStatus(Long id, String name, String abbrev, String description) {

        public static LaunchStatus of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            String name = JsonParser.text(node, "name");

            if (id == null) {
                return null;
            }

            return new LaunchStatus(id, name, JsonParser.text(45, node, "abbrev"), JsonParser.text(node, "description"));
        }
    }

    /** {@code net_precision}. */
    public record NetPrecision(Long id, String name, String abbrev, String description) {

        public static NetPrecision of(JsonNode node) {
            Long id = JsonParser.id(node, "id");

            if (id == null) {
                return null;
            }

            return new NetPrecision(
                    id,
                    JsonParser.text(45, node, "name"),
                    JsonParser.text(45, node, "abbrev"),
                    JsonParser.text(255, node, "description")
            );
        }
    }

    /** {@code launch}. */
    public record Launch(
            String id,
            String slug,
            String flightclubUrl,
            String name,
            Instant lastUpdated,
            Instant net,
            Instant windowEnd,
            Instant windowStart,
            Double probability,
            String weatherConcerns,
            Long agencyId,
            Long rocketId,
            Long missionId,
            Long launchPadId,
            Long statusId,
            Long netPrecisionId
    ) {

        public static Launch of(JsonNode node) {
            String id = JsonParser.text(45, node, "id");
            String slug = JsonParser.text(node, "slug");

            if (id == null || slug == null) {
                return null;
            }

            return new Launch(
                    id,
                    slug,
                    JsonParser.text(node, "flightclub_url"),
                    JsonParser.text(node, "name"),
                    JsonParser.instant(node, "last_updated"),
                    JsonParser.instant(node, "net"),
                    JsonParser.instant(node, "window_end"),
                    JsonParser.instant(node, "window_start"),
                    JsonParser.dbl(node, "probability"),
                    JsonParser.text(node, "weather_concerns"),
                    JsonParser.id(node, "launch_service_provider", "id"),
                    JsonParser.id(node, "rocket", "id"),
                    JsonParser.id(node, "mission", "id"),
                    JsonParser.id(node, "pad", "id"),
                    JsonParser.id(node, "status", "id"),
                    JsonParser.id(node, "net_precision", "id"));
        }
    }

    /**
     * {@code videos}. Upstream gives no id, so the key is the entry's priority within its launch
     * — the same scheme {@code VideoEntity} used.
     */
    public record Video(
            String id,
            Long priority,
            String source,
            String publisher,
            String title,
            String description,
            String featureImage,
            String videoUrl,
            String launchId
    ) {

        public static Video of(JsonNode node, String launchId) {
            Integer priority = JsonParser.integer(node, "priority");
            String id = CompositeKey.from(priority, launchId, String::valueOf);

            if (id == null) {
                return null;
            }

            return new Video(
                    id,
                    priority.longValue(),
                    JsonParser.text(45, node, "source"),
                    JsonParser.text(45, node, "publisher"),
                    JsonParser.text(node, "title"),
                    JsonParser.text(node, "description"),
                    JsonParser.text(node, "feature_image"),
                    JsonParser.text(node, "url"),
                    launchId);
        }
    }

    /** {@code updates}. */
    public record Update(
            Long id,
            String profileImage,
            String comment,
            String infoUrl,
            String createdBy,
            Instant createdOn,
            String launchId
    ) {

        public static Update of(JsonNode node, String launchId) {
            Long id = JsonParser.id(node, "id");

            if (id == null || launchId == null) {
                return null;
            }

            return new Update(
                    id,
                    JsonParser.text(node, "profile_image"),
                    JsonParser.text(node, "comment"),
                    JsonParser.text(node, "info_url"),
                    JsonParser.text(node, "created_by"),
                    JsonParser.instant(node, "created_on"),
                    launchId);
        }
    }

    /** {@code info_urls}. Keyed like videos — no upstream id. */
    public record InfoUrl(
            String id,
            Integer priority,
            String source,
            String title,
            String description,
            String featureImage,
            String url,
            String launchId
    ) {

        public static InfoUrl of(JsonNode node, String launchId) {
            Integer priority = JsonParser.integer(node, "priority");
            String id = CompositeKey.from(priority, launchId, String::valueOf);

            if (id == null) {
                return null;
            }

            return new InfoUrl(
                    id,
                    priority,
                    JsonParser.text(node, "source"),
                    JsonParser.text(node, "title"),
                    JsonParser.text(node, "description"),
                    JsonParser.text(node, "feature_image"),
                    JsonParser.text(node, "url"),
                    launchId);
        }
    }

    /** {@code mission_patches}. */
    public record MissionPatch(
            Long id,
            Integer priority,
            String name,
            String imageUrl,
            String launchId
    ) {

        public static MissionPatch of(JsonNode node, String launchId) {
            Long id = JsonParser.id(node, "id");
            Integer priority = JsonParser.integer(node, "priority");
            String name = JsonParser.text(node, "name");
            String imageUrl = JsonParser.text(node, "image_url");

            if (id == null) {
                return null;
            }

            return new MissionPatch(id, priority, name, imageUrl, launchId);
        }
    }
}
