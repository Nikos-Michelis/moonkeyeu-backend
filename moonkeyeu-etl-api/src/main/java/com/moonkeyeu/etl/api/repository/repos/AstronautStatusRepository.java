package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.crew.AstronautStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for AstronautStatusEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface AstronautStatusRepository extends JpaRepository<AstronautStatusEntity, Long> {}