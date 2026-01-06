package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.images.PadImagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for LaunchPadImagesEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface LaunchPadImagesRepository extends JpaRepository<PadImagesEntity, Long> { }