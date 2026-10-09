package com.moonkeyeu.etl.api.pipeline.ll2.extract;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.ll2.Context;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.RowSink;
import com.moonkeyeu.etl.api.pipeline.ll2.Table;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.LauncherRows;

/**
 * {@code rocket.launcher_stage[]} — the boosters flown on a launch, with their status, portrait
 * and landing.
 * <p>
 * Expects {@code rocket_id} in the context.
 * <p>
 * Replaces {@code mapLaunchersStage} and {@code mapLauncherImages}. The old image method walked
 * the stage array separately from the stage method, so the two could disagree about which stages
 * existed; here they are read in one pass.
 */
public final class LauncherStageExtractor implements Extractor {

    @Override
    public void extract(JsonNode rocket, Context context, RowSink sink) {
        for (JsonNode stage : JsonParser.array(rocket, "launcher_stage")) {
            JsonNode launcherNode = JsonParser.at(stage, "launcher");

            LauncherRows.LauncherStatus status =
                    LauncherRows.LauncherStatus.of(JsonParser.at(launcherNode, "status"));
            if (status != null) {
                sink.emit(Table.LAUNCHER_STATUS, status.id(), status);
            }

            LauncherRows.Launcher launcher = LauncherRows.Launcher.of(launcherNode);
            if (launcher != null) {
                sink.emit(Table.LAUNCHER, launcher.id(), launcher);
                CommonNode.image(launcherNode, Table.LAUNCHER_IMAGES, launcher.id(), sink, "image");
            }

            CommonNode.landing(JsonParser.at(stage, "landing"), sink);

            LauncherRows.LauncherStage stageRow = LauncherRows.LauncherStage.of(stage, context.rocketId());
            if (stageRow != null) {
                sink.emit(Table.LAUNCHER_STAGE, stageRow.id(), stageRow);
            }
        }
    }
}
