package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.images.ProgramsImagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for ProgramsImagesEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface ProgramsImagesRepository extends JpaRepository<ProgramsImagesEntity, Long> {}