package com.moonkeyeu.etl.api.configuration.files;

import lombok.Getter;

@Getter
public enum JsonGroup {
    JSON_AGENCIES("agencies", "agencies.json"),
    JSON_LAUNCHES("missions", "missions.json");

    private final String folder;
    private final String file;

    JsonGroup(String folder, String file) {
        this.folder = folder;
        this.file = file;
    }
}
