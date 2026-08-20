package com.moonkeyeu.etl.api.pipeline.ll2.extract;

import com.fasterxml.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.ll2.Context;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.RowSink;
import com.moonkeyeu.etl.api.pipeline.ll2.Table;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.RocketRows;

/**
 * {@code launch.rocket} — the configuration, its manufacturer and portrait, and the stages beneath.
 * <p>
 * Adds {@code rocket_id} to the context for the stage extractors.
 * <p>
 * The rocket's image is the one image in the schema whose foreign key runs the other way:
 * {@code rocket_configuration.image_id} points at {@code rocket_conf_images}, so the image row has
 * no owner column and must be written before the configuration that references it. Table load order
 * already guarantees that.
 */
public final class RocketExtractor implements Extractor {

    private final LauncherStageExtractor launcherStageExtractor;
    private final SpacecraftStageExtractor spacecraftStageExtractor;

    public RocketExtractor(LauncherStageExtractor launcherStageExtractor,
                           SpacecraftStageExtractor spacecraftStageExtractor) {
        this.launcherStageExtractor = launcherStageExtractor;
        this.spacecraftStageExtractor = spacecraftStageExtractor;
    }

    @Override
    public void extract(JsonNode launch, Context context, RowSink sink) {
        JsonNode rocketNode = JsonParser.at(launch, "rocket");
        Long rocketId = JsonParser.id(rocketNode, "id");
        if (rocketId == null) {
            return;
        }

        JsonNode configNode = JsonParser.at(rocketNode, "configuration");

        CommonNode.image(configNode, Table.ROCKET_CONF_IMAGES, null, sink, "image");
        CommonNode.agency(JsonParser.at(configNode, "manufacturer"), sink);

        RocketRows.RocketConfiguration config = RocketRows.RocketConfiguration.of(configNode);
        if (config != null) {
            sink.emit(Table.ROCKET_CONFIGURATION, config.id(), config);
        }

        RocketRows.Rocket rocket = RocketRows.Rocket.of(rocketNode);
        if (rocket != null) {
            sink.emit(Table.ROCKET, rocket.id(), rocket);
        }

        Context rocketContext = context.with(Context.ROCKET_ID, rocketId);
        launcherStageExtractor.extract(rocketNode, rocketContext, sink);
        spacecraftStageExtractor.extract(rocketNode, rocketContext, sink);
    }
}
