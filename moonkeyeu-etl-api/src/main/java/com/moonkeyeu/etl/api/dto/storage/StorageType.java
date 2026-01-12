package com.moonkeyeu.etl.api.dto.storage;

import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreProviderException;
import lombok.Getter;


@Getter
public enum StorageType {
    LOCAL_STORAGE,
    S3_STORAGE,;

    public static StorageType from(String value) {
        try {
            return StorageType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidStoreProviderException("Unsupported storage type: " + value);
        }
    }
}
