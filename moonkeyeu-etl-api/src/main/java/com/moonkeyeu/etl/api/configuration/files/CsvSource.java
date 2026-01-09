package com.moonkeyeu.etl.api.configuration.files;

public enum CsvSource {
    RAW_LAUNCHES_CSV("space-missions.csv"),
    RAW_AGENCIES_CSV("all-agencies.csv"),
    RAW_BOOSTERS_CSV("rocket-boosters.csv"),
    RAW_SPACECRAFT_STAGE_CSV("spacecraft-stage.csv"),
    RAW_ASTRONAUTS_CSV("astronauts.csv"),
    RAW_MISSION_PATCHES_CSV("mission-patches.csv"),
    RAW_ASTRONAUTS_COUNTRIES_CSV("astronaut-countries.csv"),
    RAW_AGENCY_COUNTRIES_CSV("agency-countries.csv"),
    RAW_MISSIONS_AGENCIES_CSV("mission-agencies.csv"),
    RAW_PROGRAMS_CSV("programs.csv"),
    RAW_VIDEO_CSV("launch-video.csv"),
    RAW_UPDATES_CSV("launch-updates.csv"),
    RAW_INFO_URLS_CSV("info-url.csv"),
    RAW_SOCIAL_MEDIA_CSV("social-media.csv"),
    LAUNCH_PROVIDERS_COUNTRIES_CSV("launch-provider-has-countries.csv"),
    MANUFACTURER_COUNTRIES_CSV("manufacturer-has-countries.csv"),
    ASTRONAUTS_COUNTRIES_CSV("astronaut-has-countries.csv"),
    PROGRAMS_AGENCIES_CSV("programs-has-agencies.csv"),
    RAW_AGENCIES_IMAGES_CSV("agencies-images.csv"),
    RAW_ASTRONAUT_IMAGES_CSV("astronaut-images.csv"),
    RAW_LAUNCHER_IMAGES_CSV("launcher-images.csv"),
    RAW_PAD_IMAGES_CSV("pad-images.csv"),
    RAW_ROCKET_CONFIGURATION_IMAGES_CSV("rocket-config-images.csv"),
    RAW_SPACECRAFT_IMAGES_CSV("spacecraft-images.csv"),
    RAW_PROGRAMS_IMAGES_CSV("programs-images.csv");


    private final String csvSource;

    CsvSource(String csvSource) {
        this.csvSource = csvSource;
    }

    public String getCsvFile() {
        return this.csvSource;
    }
}
