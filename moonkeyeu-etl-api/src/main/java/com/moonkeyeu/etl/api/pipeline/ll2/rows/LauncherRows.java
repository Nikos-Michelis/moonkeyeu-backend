package com.moonkeyeu.etl.api.pipeline.ll2.rows;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;

import java.time.Instant;

/** Rows for {@code launcher_status}, {@code launcher} and {@code launcher_stage}. */
public final class LauncherRows {

    /** {@code launcher_status}, read from {@code launcher.status}. */
    public record LauncherStatus(Long id, String name) {

        public static LauncherStatus of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            if (id == null) {
                return null;
            }
            return new LauncherStatus(id, JsonParser.text(node, "name"));
        }
    }

    /** {@code launcher} — a physical booster, reused across launches. */
    public record Launcher(
            Long id,
            String details,
            Boolean flightProven,
            String serialNumber,
            Integer successfulLandings,
            Integer attemptedLandings,
            Integer flights,
            Instant lastLaunchDate,
            Instant firstLaunchDate,
            Long statusId
    ) {

        public static Launcher of(JsonNode node) {
            Long id = JsonParser.id(node, "id");
            if (id == null) {
                return null;
            }
            return new Launcher(
                    id,
                    JsonParser.text(node, "details"),
                    JsonParser.bool(node, "flight_proven"),
                    JsonParser.text(45, node, "serial_number"),
                    JsonParser.integer(node, "successful_landings"),
                    JsonParser.integer(node, "attempted_landings"),
                    JsonParser.integer(node, "flights"),
                    JsonParser.instant(node, "last_launch_date"),
                    JsonParser.instant(node, "first_launch_date"),
                    JsonParser.id(node, "status", "id"));
        }
    }

    /** {@code launcher_stage} — one booster's participation in one launch. */
    public record LauncherStage(
            Long id,
            String type,
            Boolean reused,
            String launcherFlightNumber,
            Long rocketId,
            Long launcherId,
            Long landingId
    ) {

        public static LauncherStage of(JsonNode node, Long rocketId) {
            Long id = JsonParser.id(node, "id");
            String type = JsonParser.text(45, node, "type");
            // type is NOT NULL in the schema.
            if (id == null || type == null) {
                return null;
            }
            return new LauncherStage(
                    id,
                    type,
                    JsonParser.bool(node, "reused"),
                    JsonParser.text(45, node, "launcher_flight_number"),
                    rocketId,
                    JsonParser.id(node, "launcher", "id"),
                    JsonParser.id(node, "landing", "id"));
        }
    }
}