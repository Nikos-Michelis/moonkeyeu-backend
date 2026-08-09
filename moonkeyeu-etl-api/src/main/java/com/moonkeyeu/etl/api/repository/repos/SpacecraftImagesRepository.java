package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.images.SpacecraftImagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for SpacecraftImagesEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface SpacecraftImagesRepository extends JpaRepository<SpacecraftImagesEntity, Long> {}