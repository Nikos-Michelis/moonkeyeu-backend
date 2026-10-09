package com.moonkeyeu.etl.api.configuration.batch.jobs;

import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreOperationException;
import lombok.Getter;

@Getter
public enum StoreOperation {
    S3_UPLOAD,
    LOCAL_SAVE,
    GET_URL;

    public static StoreOperation from(String value) throws InvalidStoreOperationException {
        try {
            if (value == null || value.isEmpty()) {
                throw new InvalidStoreOperationException("Store operation should not be null or empty");
            }

            return StoreOperation.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidStoreOperationException("Unsupported operation type: " + value);
        }
    }
}
