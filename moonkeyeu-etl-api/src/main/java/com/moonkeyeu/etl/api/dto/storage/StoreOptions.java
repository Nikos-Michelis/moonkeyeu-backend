package com.moonkeyeu.etl.api.dto.storage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreOptions {
    private StorageType storage;
    private StoreOperation operation;
    private CleanupType cleanupType;
}
