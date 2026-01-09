package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.images.SpacecraftImagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for SpacecraftImagesEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface SpacecraftImagesRepository extends JpaRepository<SpacecraftImagesEntity, Long> {
    
    // Custom query methods can be added here
    // Example: List<SpacecraftImagesEntity> findByName(String name);
    // Example: Optional<SpacecraftImagesEntity> findById(Long id);
    
}