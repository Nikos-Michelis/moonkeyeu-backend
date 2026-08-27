package com.moonkeyeu.etl.api.pipeline.ll2.extract;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.ll2.Context;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.RowSink;
import com.moonkeyeu.etl.api.pipeline.ll2.Table;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.SpacecraftRows;

/**
 * {@code rocket.spacecraft_stage[]} — the capsule flown on a launch, its configuration, its
 * landing, and the crew aboard it.
 * <p>
 * Expects {@code rocket_id} and {@code launch_id} in the context; adds
 * {@code spacecraft_stage_id} for the crew beneath it.
 */
public final class SpacecraftStageExtractor implements Extractor {

    private final CrewExtractor crewExtractor;

    public SpacecraftStageExtractor(CrewExtractor crewExtractor) {
        this.crewExtractor = crewExtractor;
    }

    @Override
    public void extract(JsonNode rocket, Context context, RowSink sink) {
        for (JsonNode stage : JsonParser.array(rocket, "spacecraft_stage")) {
            JsonNode spacecraftNode = JsonParser.at(stage, "spacecraft");
            JsonNode configNode = JsonParser.at(spacecraftNode, "spacecraft_config");

            SpacecraftRows.SpacecraftType type = SpacecraftRows.SpacecraftType.of(JsonParser.at(configNode, "type"));
            if (type != null) {
                sink.emit(Table.SPACECRAFT_TYPE, type.id(), type);
            }

            CommonNode.agency(JsonParser.at(configNode, "agency"), sink);

            SpacecraftRows.SpacecraftConfiguration config = SpacecraftRows.SpacecraftConfiguration.of(configNode);
            if (config != null) {
                sink.emit(Table.SPACECRAFT_CONFIGURATION, config.id(), config);
                CommonNode.image(configNode, Table.SPACECRAFT_CONF_IMAGES, config.id(), sink, "image");
            }

            SpacecraftRows.SpacecraftStatus status = SpacecraftRows.SpacecraftStatus.of(JsonParser.at(spacecraftNode, "status"));
            if (status != null) {
                sink.emit(Table.SPACECRAFT_STATUS, status.id(), status);
            }

            SpacecraftRows.Spacecraft spacecraft = SpacecraftRows.Spacecraft.of(spacecraftNode);
            if (spacecraft != null) {
                sink.emit(Table.SPACECRAFT, spacecraft.id(), spacecraft);
            }

            CommonNode.landing(JsonParser.at(stage, "landing"), sink);

            SpacecraftRows.SpacecraftStage stageRow = SpacecraftRows.SpacecraftStage.of(stage, context.rocketId());
            if (stageRow == null) {
                continue;
            }
            sink.emit(Table.SPACECRAFT_STAGE, stageRow.id(), stageRow);

            Context stageContext = context.with(Context.SPACECRAFT_STAGE_ID, stageRow.id());
            for (JsonNode crew : JsonParser.array(stage, "launch_crew")) {
                crewExtractor.extract(crew, stageContext, sink);
            }
        }
    }
}
