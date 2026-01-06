package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.images.AstronautImagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for AstronautImagesEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface AstronautImagesRepository extends JpaRepository<AstronautImagesEntity, Long> { }