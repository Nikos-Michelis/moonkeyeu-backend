package com.moonkeyeu.etl.api.pipeline.ll2.rows;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.CompositeKey;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Rows for {@code astronaut_status}, {@code astronaut}, {@code role}, {@code crew_member},
 * {@code social_media} and {@code astronaut_has_country}.
 */
public final class CrewRows {

    /** {@code astronaut_status}, read from {@code astronaut.status}. */
    public record AstronautStatus(Long id, String name) {

        public static AstronautStatus of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            String name = JsonParser.text(node, "name");
            if (id == null || name == null) {
                return null;
            }
            return new AstronautStatus(id, name);
        }
    }

    /** {@code astronaut}. Upstream names the wiki field {@code wiki}, the column is {@code wiki_url}. */
    public record Astronaut(
            Long id,
            String name,
            Boolean inSpace,
            LocalDate dateOfDeath,
            LocalDate dateOfBirth,
            Integer age,
            String bio,
            String wikiUrl,
            Instant lastFlight,
            Instant firstFlight,
            Long statusId,
            Long agencyId
    ) {

        public static Astronaut of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            String name = JsonParser.text(node, "name");
            if (id == null || name == null) {
                return null;
            }
            return new Astronaut(
                    id,
                    name,
                    JsonParser.bool(node, "in_space"),
                    JsonParser.date(node, "date_of_death"),
                    JsonParser.date(node, "date_of_birth"),
                    JsonParser.integer(node, "age"),
                    JsonParser.text(node, "bio"),
                    JsonParser.text(node, "wiki"),
                    JsonParser.instant(node, "last_flight"),
                    JsonParser.instant(node, "first_flight"),
                    JsonParser.id(node, "status", "id"),
                    JsonParser.id(node, "agency", "id"));
        }
    }

    /**
     * {@code role}, read from {@code launch_crew[].role}.
     * <p>
     * The name lives under the key {@code role}, not {@code name} — an upstream quirk that the old
     * {@code Role} DTO also encoded.
     */
    public record Role(Long id, String name) {

        public static Role of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            String name = JsonParser.text(node, "role");
            if (id == null || name == null) {
                return null;
            }
            return new Role(id, name);
        }
    }

    /** {@code crew_member} — one astronaut aboard one spacecraft stage of one launch. */
    public record CrewMember(
            Long id,
            Long astronautId,
            Long roleId,
            Long spacecraftStageId,
            String launchId
    ) {

        public static CrewMember of(JsonNode node, Long spacecraftStageId, String launchId) {
            Long id = JsonParser.id(node, "id");
            Long astronautId = JsonParser.id(node, "astronaut", "id");

            if (id == null || astronautId == null) {
                return null;
            }
            return new CrewMember(
                    id,
                    astronautId,
                    JsonParser.id(node, "role", "id"),
                    spacecraftStageId,
                    launchId);
        }
    }

    /**
     * {@code social_media}. The row's own id and url come from the link, the display name from the
     * nested {@code social_media} object.
     */
    public record SocialMedia(Long id, String name, String mediaUrl, Long astronautId) {

        public static SocialMedia of(JsonNode node, Long astronautId) {
            Long id = JsonParser.id(node, "id");
            String mediaUrl = JsonParser.text(500, node, "url");

            if (id == null || mediaUrl == null || astronautId == null) {
                return null;
            }
            return new SocialMedia(id, JsonParser.text(node, "social_media", "name"), mediaUrl, astronautId);
        }
    }

    /** {@code astronaut_has_country}, from the astronaut's {@code nationality} array. */
    public record AstronautHasCountry(Long id, Long astronautId, Long countryId) {

        public static AstronautHasCountry of(Long astronautId, Long countryId) {
            Long id = CompositeKey.from(astronautId, countryId, Long::parseLong);
            if (id == null) {
                return null;
            }
            return new AstronautHasCountry(id, astronautId, countryId);
        }
    }
}
