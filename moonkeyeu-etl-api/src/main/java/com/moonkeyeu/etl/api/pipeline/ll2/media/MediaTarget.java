package com.moonkeyeu.etl.api.pipeline.ll2.media;

import lombok.Getter;

/**
 * Every column holding an image URL that the pipeline copies into our own storage.
 */
@Getter
public enum MediaTarget {

    ROCKET_CONF_IMAGES("rocket_conf_images", "image_url", "rockets"),
    LAUNCH_PAD_IMAGES("launch_pad_images", "image_url", "pads"),
    LAUNCHER_IMAGES("launcher_images", "image_url", "launchers"),
    SPACECRAFT_CONF_IMAGES("spacecraft_conf_images", "image_url", "spacecraft"),
    ASTRONAUT_IMAGES("astronaut_images", "image_url", "astronauts"),
    AGENCIES_IMAGES("agencies_images", "image_url", "agencies"),
    PROGRAMS_IMAGES("programs_images", "image_url", "programs"),
    MISSION_PATCHES("mission_patches", "image_url", "missions-patches"),
    LAUNCH_PAD_MAP("launch_pad", "map_image", "pads-locations");

    private final String tableName;
    private final String urlColumn;
    private final String folder;

    MediaTarget(String tableName, String urlColumn, String folder) {
        this.tableName = tableName;
        this.urlColumn = urlColumn;
        this.folder = folder;
    }
}
