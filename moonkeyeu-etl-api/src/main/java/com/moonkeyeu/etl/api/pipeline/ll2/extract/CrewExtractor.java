package com.moonkeyeu.etl.api.pipeline.ll2.extract;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.ll2.Context;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;
import com.moonkeyeu.etl.api.pipeline.core.RowSink;
import com.moonkeyeu.etl.api.pipeline.ll2.Table;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.CrewRows;

/**
 * One element of {@code launch_crew[]}: an astronaut, their role, agency, nationalities, social
 * links and portrait.
 * <p>
 * Expects {@code spacecraft_stage_id} and {@code launch_id} in the context.
 * <p>
 * This fixes a data-loss bug in the code it replaces. {@code mapAstronautImages} looped over the
 * whole crew array and {@code return}ed on the first astronaut carrying an image, so a four-person
 * crew persisted one portrait and silently dropped the other three. Extracting per crew member
 * makes that mistake unrepresentable.
 */
public final class CrewExtractor implements Extractor {

    @Override
    public void extract(JsonNode crew, Context context, RowSink sink) {
        JsonNode astronautNode = JsonParser.at(crew, "astronaut");
        Long astronautId = JsonParser.id(astronautNode, "id");

        if (astronautId != null) {
            CrewRows.AstronautStatus status = CrewRows.AstronautStatus.of(JsonParser.at(astronautNode, "status"));
            if (status != null) {
                sink.emit(Table.ASTRONAUT_STATUS, status.id(), status);
            }

            CommonNode.agency(JsonParser.at(astronautNode, "agency"), sink);

            CrewRows.Astronaut astronaut = CrewRows.Astronaut.of(astronautNode);
            if (astronaut != null) {
                sink.emit(Table.ASTRONAUT, astronaut.id(), astronaut);
            }

            CommonNode.image(astronautNode, Table.ASTRONAUT_IMAGES, astronautId, sink, "image");

            for (JsonNode nationality : JsonParser.array(astronautNode, "nationality")) {
                Long countryId = CommonNode.country(nationality, sink);
                CrewRows.AstronautHasCountry join =
                        CrewRows.AstronautHasCountry.of(astronautId, countryId);
                if (join != null) {
                    sink.emit(Table.ASTRONAUT_HAS_COUNTRY, join.id(), join);
                }
            }

            for (JsonNode link : JsonParser.array(astronautNode, "social_media_links")) {
                CrewRows.SocialMedia social = CrewRows.SocialMedia.of(link, astronautId);
                if (social != null) {
                    sink.emit(Table.SOCIAL_MEDIA, social.id(), social);
                }
            }
        }

        CrewRows.Role role = CrewRows.Role.of(JsonParser.at(crew, "role"));
        if (role != null) {
            sink.emit(Table.ROLE, role.id(), role);
        }

        CrewRows.CrewMember member = CrewRows.CrewMember.of(crew, context.spacecraftStageId(), context.launchId());
        if (member != null) {
            sink.emit(Table.CREW_MEMBER, member.id(), member);
        }
    }
}
