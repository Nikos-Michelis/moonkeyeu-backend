package com.moonkeyeu.etl.api.pipeline.ll2.extract;

import com.fasterxml.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.RowSink;
import com.moonkeyeu.etl.api.pipeline.ll2.Table;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.AgencyRows;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.ImageRow;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.LandingRows;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.PadRows;

/**
 * Sub-trees that appear in more than one place in the payload.
 * <p>
 * An agency hangs off a launch, a mission, a rocket manufacturer, a spacecraft manufacturer and an
 * astronaut; a landing hangs off both a booster stage and a capsule stage. Rather than duplicate
 * those traversals across extractors, they live here once.
 */
final class CommonNode {

    private CommonNode() {}

    /**
     * Writes an agency and its type.
     * <p>
     * Most call sites reach an agency through a reference that carries only a few fields. That is
     * fine: the upsert only overwrites a column when the incoming value is non-null, so a sparse
     * reference cannot blank out a row the dedicated agencies feed populated in full.
     */
    static void agency(JsonNode agencyNode, RowSink sink) {
        if (agencyNode == null || agencyNode.isMissingNode() || agencyNode.isNull()) {
            return;
        }
        AgencyRows.AgencyType type = AgencyRows.AgencyType.of(JsonParser.at(agencyNode, "type"));
        if (type != null) {
            sink.emit(Table.AGENCY_TYPE, type.id(), type);
        }
        AgencyRows.Agency agency = AgencyRows.Agency.of(agencyNode);
        if (agency != null) {
            sink.emit(Table.AGENCIES, agency.id(), agency);
        }
    }

    /** Writes an image row to the given table, keyed to its owner. */
    static void image(JsonNode parent, Table table, Long ownerId, RowSink sink, String... path) {
        ImageRow image = ImageRow.at(parent, ownerId, path);
        if (image != null) {
            sink.emit(table, image.id(), image);
        }
    }

    /**
     * Writes a landing and everything it depends on: the landing type, the zone, and the zone's
     * location. Shared by {@code launcher_stage} and {@code spacecraft_stage}, which carry the
     * identical structure.
     */
    static void landing(JsonNode landingNode, RowSink sink) {
        if (landingNode == null || landingNode.isMissingNode() || landingNode.isNull()) {
            return;
        }

        LandingRows.LandingType type = LandingRows.LandingType.of(JsonParser.at(landingNode, "type"));
        if (type != null) {
            sink.emit(Table.LANDING_TYPE, type.id(), type);
        }

        JsonNode zoneNode = JsonParser.at(landingNode, "landing_location");
        PadRows.Location location = PadRows.Location.of(JsonParser.at(zoneNode, "location"));
        if (location != null) {
            sink.emit(Table.LOCATION, location.id(), location);
        }

        LandingRows.LandingZone zone = LandingRows.LandingZone.of(zoneNode);
        if (zone != null) {
            sink.emit(Table.LANDING_ZONE, zone.id(), zone);
        }

        LandingRows.Landing landing = LandingRows.Landing.of(landingNode);
        if (landing != null) {
            sink.emit(Table.LANDING, landing.id(), landing);
        }
    }

    /** Writes a country row. The join row is the caller's business — the pairing differs per parent. */
    static Long country(JsonNode countryNode, RowSink sink) {
        AgencyRows.Country country = AgencyRows.Country.of(countryNode);
        if (country == null) {
            return JsonParser.id(countryNode, "id");
        }
        sink.emit(Table.COUNTRY, country.id(), country);
        return country.id();
    }
}
