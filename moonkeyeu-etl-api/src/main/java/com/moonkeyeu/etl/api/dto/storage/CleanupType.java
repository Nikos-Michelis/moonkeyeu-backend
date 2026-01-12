package com.moonkeyeu.etl.api.dto.storage;

import com.moonkeyeu.etl.api.settings.exceptions.InvalidCleanupOperationException;
import lombok.Getter;

@Getter
public enum CleanupType {
    ALL,
    ONLY_CSV,
    ONLY_JSON;

    public static CleanupType from(String value) {
        try {
            return CleanupType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCleanupOperationException("Unsupported cleanup type: " + value);
        }
    }
}
