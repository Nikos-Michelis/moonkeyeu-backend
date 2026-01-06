package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.images.LauncherImagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for LauncherImagesEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface LauncherImagesRepository extends JpaRepository<LauncherImagesEntity, Long> { }