package com.moonkeyeu.etl.api.dto.storage;

import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreProviderException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum StorageType {
    LOCAL_STORAGE("LOCAL_STRATEGY"),
    S3_STORAGE("S3_STRATEGY");

    private final String type;

    public static StorageType from(String value) throws InvalidStoreProviderException {
        try {

            if (value == null || value.isEmpty()) {
                throw new InvalidStoreProviderException("Storage type should not be null or empty");
            }

            return StorageType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidStoreProviderException("Unsupported storage type: " + value);
        }
    }
}
