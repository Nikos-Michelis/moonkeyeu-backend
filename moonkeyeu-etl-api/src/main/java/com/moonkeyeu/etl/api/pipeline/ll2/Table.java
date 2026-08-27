package com.moonkeyeu.etl.api.pipeline.ll2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Every table the ETL writes, ordered so that a parent is always written before any child that
 * references it.
 */
public enum Table {

    /* ---- level 0: no outbound foreign keys ---- */
    ROCKET_CONF_IMAGES("rocket_conf_images", 10),
    AGENCY_TYPE("agency_type", 20),
    SPACECRAFT_TYPE("spacecraft_type", 30),
    SPACECRAFT_STATUS("spacecraft_status", 40),
    ORBIT("orbit", 50),
    LAUNCH_STATUS("launch_status", 60),
    NET_PRECISION("net_precision", 70),
    LOCATION("location", 80),
    LANDING_TYPE("landing_type", 90),
    ASTRONAUT_STATUS("astronaut_status", 100),
    ROLE("role", 110),
    LAUNCHER_STATUS("launcher_status", 120),
    COUNTRY("country", 130),
    PROGRAM_TYPE("program_type", 140),

    /* ---- level 1: depend only on level 0 ---- */
    AGENCIES("agencies", 150),
    MISSION("mission", 160),
    SPACECRAFT_CONFIGURATION("spacecraft_configuration", 170),
    ROCKET_CONFIGURATION("rocket_configuration", 180),
    LAUNCH_PAD("launch_pad", 190),
    LANDING_ZONE("landing_zone", 200),
    LAUNCHER("launcher", 210),
    ASTRONAUT("astronaut", 220),
    PROGRAMS("programs", 230),

    /* ---- level 2 ---- */
    ROCKET("rocket", 240),
    LANDING("landing", 250),
    SPACECRAFT("spacecraft", 260),
    MISSION_HAS_AGENCIES("mission_has_agencies", 270),
    PAD_HAS_AGENCIES("pad_has_agencies", 280),
    AGENCIES_HAS_COUNTRY("agencies_has_country", 290),
    ASTRONAUT_HAS_COUNTRY("astronaut_has_country", 300),
    PROGRAMS_HAS_AGENCIES("programs_has_agencies", 310),
    AGENCIES_IMAGES("agencies_images", 320),
    SPACECRAFT_CONF_IMAGES("spacecraft_conf_images", 330),
    LAUNCHER_IMAGES("launcher_images", 340),
    ASTRONAUT_IMAGES("astronaut_images", 350),
    LAUNCH_PAD_IMAGES("launch_pad_images", 360),
    PROGRAMS_IMAGES("programs_images", 370),
    SOCIAL_MEDIA("social_media", 380),

    /* ---- level 3 ---- */
    LAUNCH("launch", 390),
    SPACECRAFT_STAGE("spacecraft_stage", 400),
    LAUNCHER_STAGE("launcher_stage", 410),

    /* ---- level 4: reference launch ---- */
    VIDEOS("videos", 420),
    UPDATES("updates", 430),
    INFO_URLS("info_urls", 440),
    MISSION_PATCHES("mission_patches", 450),
    CREW_MEMBER("crew_member", 460),
    LAUNCH_HAS_PROGRAMS("launch_has_programs", 470);

    private static final List<Table> LOAD_ORDER = Arrays.stream(values())
            .sorted(Comparator.comparingInt(Table::loadOrder))
            .toList();

    private final String tableName;
    private final int loadOrder;

    Table(String tableName, int loadOrder) {
        this.tableName = tableName;
        this.loadOrder = loadOrder;
    }

    public String tableName() {
        return tableName;
    }

    public int loadOrder() {
        return loadOrder;
    }

    /** Tables in the order they must be written for foreign keys to resolve. */
    public static List<Table> inLoadOrder() {
        return LOAD_ORDER;
    }
}