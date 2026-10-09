package com.moonkeyeu.etl.api.pipeline.ll2.extract;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.ll2.Context;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.RowSink;
import com.moonkeyeu.etl.api.pipeline.ll2.Table;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.AgencyRows;

/**
 * Root of the agencies feed — one element of {@code results[]} from the agencies endpoint.
 * <p>
 * Replaces {@code JsonObjectMapper.JsonToAgenciesMapper} and the four methods it called.
 */
public final class AgencyExtractor implements Extractor {

    @Override
    public void extract(JsonNode agency, Context context, RowSink sink) {
        Long agencyId = JsonParser.id(agency, "id");
        if (agencyId == null) {
            return;
        }

        CommonNode.agency(agency, sink);

        CommonNode.image(agency, Table.AGENCIES_IMAGES, agencyId, sink, "logo");

        for (JsonNode countryNode : JsonParser.array(agency, "country")) {
            Long countryId = CommonNode.country(countryNode, sink);
            AgencyRows.AgencyHasCountry join = AgencyRows.AgencyHasCountry.of(agencyId, countryId);
            if (join != null) {
                sink.emit(Table.AGENCIES_HAS_COUNTRY, join.id(), join);
            }
        }
    }
}
