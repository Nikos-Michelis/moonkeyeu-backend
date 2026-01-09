package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.images.AgenciesImagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for AgenciesImagesEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface AgenciesImagesRepository extends JpaRepository<AgenciesImagesEntity, Long> {}