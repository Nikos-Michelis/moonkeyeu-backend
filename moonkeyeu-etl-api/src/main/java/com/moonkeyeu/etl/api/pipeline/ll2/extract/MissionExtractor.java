package com.moonkeyeu.etl.api.pipeline.ll2.extract;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.ll2.Context;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.RowSink;
import com.moonkeyeu.etl.api.pipeline.ll2.Table;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.MissionRows;

/**
 * {@code launch.mission} — the mission, its orbit, and the agencies participating in it.
 * <p>
 * Replaces {@code mapMissionAgencies}, which emitted agency rows into the mission-agencies CSV and
 * therefore only ever produced the join, never the agency itself.
 */
public final class MissionExtractor implements Extractor {

    @Override
    public void extract(JsonNode launch, Context context, RowSink sink) {
        JsonNode missionNode = JsonParser.at(launch, "mission");
        Long missionId = JsonParser.id(missionNode, "id");
        if (missionId == null) {
            return;
        }

        MissionRows.Orbit orbit = MissionRows.Orbit.of(JsonParser.at(missionNode, "orbit"));
        if (orbit != null) {
            sink.emit(Table.ORBIT, orbit.id(), orbit);
        }

        MissionRows.Mission mission = MissionRows.Mission.of(missionNode);
        if (mission != null) {
            sink.emit(Table.MISSION, mission.id(), mission);
        }

        for (JsonNode agencyNode : JsonParser.array(missionNode, "agencies")) {
            CommonNode.agency(agencyNode, sink);
            MissionRows.MissionHasAgencies join =
                    MissionRows.MissionHasAgencies.of(missionId, JsonParser.id(agencyNode, "id"));
            if (join != null) {
                sink.emit(Table.MISSION_HAS_AGENCIES, join.id(), join);
            }
        }
    }
}
