package com.moonkeyeu.etl.api.configuration.batch.jobs;

import com.moonkeyeu.etl.api.settings.exceptions.CleanupException;
import lombok.Getter;

@Getter
public enum CleanupType {
    ALL,
    NONE;

    public static CleanupType from(String value) {
        try {
            return CleanupType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CleanupException("Unsupported cleanup type: " + value);
        }
    }
}
