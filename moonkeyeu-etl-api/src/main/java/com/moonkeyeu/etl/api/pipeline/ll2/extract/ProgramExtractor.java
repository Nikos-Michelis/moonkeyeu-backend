package com.moonkeyeu.etl.api.pipeline.ll2.extract;

import com.fasterxml.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.ll2.Context;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.RowSink;
import com.moonkeyeu.etl.api.pipeline.ll2.Table;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.ProgramRows;

/**
 * {@code launch.program[]} — the programme a launch belongs to, its type, portrait, participating
 * agencies, and the launch-to-programme join.
 * <p>
 * Expects {@code launch_id} in the context.
 */
public final class ProgramExtractor implements Extractor {

    @Override
    public void extract(JsonNode launch, Context context, RowSink sink) {
        for (JsonNode programNode : JsonParser.array(launch, "program")) {
            ProgramRows.ProgramType type = ProgramRows.ProgramType.of(JsonParser.at(programNode, "type"));
            if (type != null) {
                sink.emit(Table.PROGRAM_TYPE, type.id(), type);
            }

            ProgramRows.Program program = ProgramRows.Program.of(programNode);
            if (program == null) {
                continue;
            }
            sink.emit(Table.PROGRAMS, program.id(), program);

            CommonNode.image(programNode, Table.PROGRAMS_IMAGES, program.id(), sink, "image");

            for (JsonNode agencyNode : JsonParser.array(programNode, "agencies")) {
                CommonNode.agency(agencyNode, sink);
                ProgramRows.ProgramHasAgencies agencyJoin = ProgramRows.ProgramHasAgencies.of(program.id(), JsonParser.id(agencyNode, "id"));
                if (agencyJoin != null) {
                    sink.emit(Table.PROGRAMS_HAS_AGENCIES, agencyJoin.id(), agencyJoin);
                }
            }

            ProgramRows.LaunchHasProgram launchJoin = ProgramRows.LaunchHasProgram.of(context.launchId(), program.id());
            if (launchJoin != null) {
                sink.emit(Table.LAUNCH_HAS_PROGRAMS, launchJoin.id(), launchJoin);
            }
        }
    }
}
