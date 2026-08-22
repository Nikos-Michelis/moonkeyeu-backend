package com.moonkeyeu.etl.api.pipeline.ll2.extract;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.ll2.Context;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.RowSink;
import com.moonkeyeu.etl.api.pipeline.ll2.Table;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.PadRows;
import lombok.extern.slf4j.Slf4j;

/**
 * {@code launch.pad} — the pad, its location, its portrait, and the provider-to-pad join.
 * <p>
 * Expects {@code agency_id} in the context. Upstream does not attach an agency to a pad; the
 * association recorded here is "this launch's service provider flew from this pad", which is what
 * the previous pipeline stored, though only because {@code LaunchPadEntity.agency_id} and the
 * agency DTO's {@code agency_id} collided on the same CSV column name.
 */
@Slf4j
public final class PadExtractor implements Extractor {

    @Override
    public void extract(JsonNode launch, Context context, RowSink sink) {
        JsonNode padNode = JsonParser.at(launch, "pad");
        Long padId = JsonParser.id(padNode, "id");
        if (padId == null) {
            return;
        }

        PadRows.Location location = PadRows.Location.of(JsonParser.at(padNode, "location"));
        if (location != null) {
            sink.emit(Table.LOCATION, location.id(), location);
        }

        PadRows.LaunchPad pad = PadRows.LaunchPad.of(padNode);
        if (pad != null) {
            sink.emit(Table.LAUNCH_PAD, pad.id(), pad);
        }

        CommonNode.image(padNode, Table.LAUNCH_PAD_IMAGES, padId, sink, "image");


        for (JsonNode agencyNode : JsonParser.array(padNode, "agencies")) {
            CommonNode.agency(agencyNode, sink);
            PadRows.PadHasAgencies agencyJoin = PadRows.PadHasAgencies.of(padId, JsonParser.id(agencyNode, "id"));
            if (agencyJoin != null) {
                sink.emit(Table.PAD_HAS_AGENCIES, agencyJoin.id(), agencyJoin);
            }
        }
    }
}
