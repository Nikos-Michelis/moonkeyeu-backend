package com.moonkeyeu.etl.api.dto.storage;

import lombok.Getter;

@Getter
public enum StoreOperation {
    UPLOAD,
    GET_URL;

    public static StoreOperation from(String value) {
        return StoreOperation.valueOf(value.trim().toUpperCase());
    }
}
