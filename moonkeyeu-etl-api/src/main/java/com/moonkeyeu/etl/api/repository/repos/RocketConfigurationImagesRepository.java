package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.images.RocketImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for RocketConfigurationImagesEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface RocketConfigurationImagesRepository extends JpaRepository<RocketImageEntity, Long> {
    
    // Custom query methods can be added here
    // Example: List<RocketConfigurationImagesEntity> findByName(String name);
    // Example: Optional<RocketConfigurationImagesEntity> findById(Long id);
    
}