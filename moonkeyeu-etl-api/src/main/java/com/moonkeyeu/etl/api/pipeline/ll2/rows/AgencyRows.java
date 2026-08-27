package com.moonkeyeu.etl.api.pipeline.ll2.rows;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.CompositeKey;

/** Rows for {@code agency_type}, {@code agencies}, {@code country} and {@code agencies_has_country}. */
public final class AgencyRows {

    /** {@code agency_type}. Read from the {@code type} object nested in an agency. */
    public record AgencyType(Long id, String name) {

        public static AgencyType of(JsonNode typeNode) {
            Long id = JsonParser.id(typeNode, "id");
            String name = JsonParser.text(45, typeNode, "name");
            if (id == null) {
                return null;
            }
            return new AgencyType(id, name);
        }
    }

    /**
     * {@code agencies}.
     * <p>
     * Reached from two places: the dedicated agencies feed, which returns the full object, and
     * embedded references inside a launch ({@code launch_service_provider}, {@code mission.agencies},
     * a spacecraft configuration's manufacturer), which return a subset. Both produce this record;
     * the upsert only overwrites a column when the incoming value is non-null, so a sparse
     * reference can never blank out a row the full feed already populated.
     */
    public record Agency(
            Long id,
            String name,
            Boolean featured,
            Long typeId,
            String abbrev,
            String description,
            String administrator,
            String foundingYear,
            String launchers,
            String spacecraft,
            Integer totalLaunchCount,
            Integer consecutiveSuccessfulLaunches,
            Integer successfulLaunches,
            Integer failedLaunches,
            Integer pendingLaunches,
            Integer consecutiveSuccessfulLandings,
            Integer successfulLandings,
            Integer failedLandings,
            Integer attemptedLandings,
            String infoUrl,
            String wikiUrl
    ) {

        public static Agency of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            if (id == null) {
                return null;
            }
            return new Agency(
                    id,
                    JsonParser.text(node, "name"),
                    JsonParser.bool(node, "featured"),
                    JsonParser.id(node, "type", "id"),
                    JsonParser.text(45, node, "abbrev"),
                    JsonParser.text(node, "description"),
                    JsonParser.text(node, "administrator"),
                    JsonParser.text(4, node, "founding_year"),
                    JsonParser.text(node, "launchers"),
                    JsonParser.text(node, "spacecraft"),
                    JsonParser.integer(node, "total_launch_count"),
                    JsonParser.integer(node, "consecutive_successful_launches"),
                    JsonParser.integer(node, "successful_launches"),
                    JsonParser.integer(node, "failed_launches"),
                    JsonParser.integer(node, "pending_launches"),
                    JsonParser.integer(node, "consecutive_successful_landings"),
                    JsonParser.integer(node, "successful_landings"),
                    JsonParser.integer(node, "failed_landings"),
                    JsonParser.integer(node, "attempted_landings"),
                    JsonParser.text(node, "info_url"),
                    JsonParser.text(node, "wiki_url"));
        }
    }

    /** {@code country}. */
    public record Country(
            Long id,
            String name,
            String alpha2Code,
            String alpha3Code,
            String nationalityName,
            String nationalityNameComposed
    ) {

        public static Country of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            String name = JsonParser.text(node, "name");
            if (id == null || name == null) {
                return null;
            }
            return new Country(
                    id,
                    name,
                    JsonParser.text(45, node, "alpha_2_code"),
                    JsonParser.text(45, node, "alpha_3_code"),
                    JsonParser.text(45, node, "nationality_name"),
                    JsonParser.text(45, node, "nationality_name_composed"));
        }
    }

    /** {@code agencies_has_country}. */
    public record AgencyHasCountry(Long id, Long agencyId, Long countryId) {

        public static AgencyHasCountry of(Long agencyId, Long countryId) {
            Long id = CompositeKey.from(agencyId, countryId, Long::parseLong);
            if (id == null) {
                return null;
            }
            return new AgencyHasCountry(id, agencyId, countryId);
        }
    }
}