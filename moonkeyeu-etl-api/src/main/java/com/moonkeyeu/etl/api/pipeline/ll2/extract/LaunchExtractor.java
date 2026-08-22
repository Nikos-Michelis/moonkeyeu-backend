package com.moonkeyeu.etl.api.pipeline.ll2.extract;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.ll2.Context;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.RowSink;
import com.moonkeyeu.etl.api.pipeline.ll2.Table;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.LaunchRows;

import java.util.List;

import static com.moonkeyeu.etl.api.pipeline.ll2.extract.ArrayExtractor.path;

/**
 * Root of the launches feed — one element of {@code results[]} from the launches endpoint.
 * <p>
 * Replaces {@code JsonObjectMapper.jsonToLaunchesMapper} and the twenty-one methods it called.
 * The four media arrays hanging directly off a launch need no bespoke code at all: they are the
 * same shape and are declared, not written.
 */
public final class LaunchExtractor implements Extractor {

    private final List<Extractor> children;

    public LaunchExtractor(RocketExtractor rocketExtractor, PadExtractor padExtractor, MissionExtractor missionExtractor, ProgramExtractor programExtractor) {

        this.children = List.of(rocketExtractor, padExtractor, missionExtractor, programExtractor,

                new ArrayExtractor<>(path("vid_urls"), Table.VIDEOS,
                        (node, context) -> LaunchRows.Video.of(node, context.launchId()),
                        LaunchRows.Video::id),

                new ArrayExtractor<>(path("updates"), Table.UPDATES,
                        (node, context) -> LaunchRows.Update.of(node, context.launchId()),
                        LaunchRows.Update::id),

                new ArrayExtractor<>(path("info_urls"), Table.INFO_URLS,
                        (node, context) -> LaunchRows.InfoUrl.of(node, context.launchId()),
                        LaunchRows.InfoUrl::id),

                new ArrayExtractor<>(path("mission_patches"), Table.MISSION_PATCHES,
                        (node, context) -> LaunchRows.MissionPatch.of(node, context.launchId()),
                        LaunchRows.MissionPatch::id));
    }

    @Override
    public void extract(JsonNode launch, Context context, RowSink sink) {
        String launchId = JsonParser.text(45, launch, "id");
        if (launchId == null) {
            return;
        }

        LaunchRows.LaunchStatus status = LaunchRows.LaunchStatus.of(JsonParser.at(launch, "status"));
        if (status != null) {
            sink.emit(Table.LAUNCH_STATUS, status.id(), status);
        }

        LaunchRows.NetPrecision precision = LaunchRows.NetPrecision.of(JsonParser.at(launch, "net_precision"));
        if (precision != null) {
            sink.emit(Table.NET_PRECISION, precision.id(), precision);
        }

        LaunchRows.Launch launchRow = LaunchRows.Launch.of(launch);
        if (launchRow != null) {
            sink.emit(Table.LAUNCH, launchRow.id(), launchRow);
        }

        Context launchContext = context.with(Context.LAUNCH_ID, launchId);
        for (Extractor child : children) {
            child.extract(launch, launchContext, sink);
        }
    }
}
