package com.moonkeyeu.etl.api.configuration.files;

import lombok.Getter;

@Getter
public enum DirSource {
    DOWNLOAD_ROCKET_DIR("rockets"),
    DOWNLOAD_SPACECRAFT_DIR("spacecraft"),
    DOWNLOAD_ASTRONAUTS_DIR("astronauts"),
    DOWNLOAD_AGENCIES_DIR("agencies"),
    DOWNLOAD_LAUNCHERS_DIR("launchers"),
    DOWNLOAD_MISSIONS_PATCHES_DIR("missions-patches"),
    DOWNLOAD_PADS_DIR("pads"),
    DOWNLOAD_PADS_LOCATIONS_DIR("pads-locations"),
    DOWNLOAD_PROGRAMS_DIR("programs");

    private final String csvSource;

    DirSource(String csvSource) {
        this.csvSource = csvSource;
    }
}
