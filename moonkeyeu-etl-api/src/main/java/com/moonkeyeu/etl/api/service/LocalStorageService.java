package com.moonkeyeu.etl.api.service;

import java.nio.file.Path;

public interface LocalStorageService {
    boolean existsByKey(Path filePath);
    void save(byte[] data, Path filePath);
}
