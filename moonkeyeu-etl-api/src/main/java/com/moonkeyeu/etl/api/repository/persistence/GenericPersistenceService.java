package com.moonkeyeu.etl.api.repository.persistence;

import org.springframework.batch.item.Chunk;

import java.sql.SQLException;

public interface GenericPersistenceService {
    <T> T save(T object) throws SQLException;
    <T> void saveAll(Chunk<?> entities) throws SQLException;
}
