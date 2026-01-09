package com.moonkeyeu.etl.api.configuration.files;

import lombok.Getter;

@Getter
public enum JsonGroup {
    JSON_AGENCIES("all-agencies.json"),
    JSON_LAUNCHES("space-missions.json");

    private final String csvSource;

    JsonGroup(String csvSource) {
        this.csvSource = csvSource;
    }

    public String getJsonFile() {
        return this.csvSource;
    }
}
