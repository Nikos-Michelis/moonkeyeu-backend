package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.crew.AstronautEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for AstronautEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface AstronautRepository extends JpaRepository<AstronautEntity, Long> { }