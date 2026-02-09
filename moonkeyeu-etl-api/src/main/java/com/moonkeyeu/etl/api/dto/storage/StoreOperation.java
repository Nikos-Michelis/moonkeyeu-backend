package com.moonkeyeu.etl.api.dto.storage;

import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreOperationException;
import lombok.Getter;

@Getter
public enum StoreOperation {
    S3_UPLOAD,
    LOCAL_SAVE,
    GET_URL;

    public static StoreOperation from(String value) {
        try {
            return StoreOperation.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidStoreOperationException("Unsupported operation type: " + value);
        }
    }
}
