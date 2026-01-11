package com.moonkeyeu.etl.api.dto.storage;

import lombok.Getter;

@Getter
public enum CleanupType {
    ALL,
    ONLY_CSV,
    ONLY_JSON;

    public static CleanupType from(String value) {
        return CleanupType.valueOf(value.trim().toUpperCase());
    }
}
