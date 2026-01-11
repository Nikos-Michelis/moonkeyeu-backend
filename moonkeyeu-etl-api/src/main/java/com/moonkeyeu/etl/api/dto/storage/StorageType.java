package com.moonkeyeu.etl.api.dto.storage;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum StorageType {
    LOCAL,
    S3;

    public static StorageType from(String value) {
       return StorageType.valueOf(value.trim().toUpperCase());
    }
}
