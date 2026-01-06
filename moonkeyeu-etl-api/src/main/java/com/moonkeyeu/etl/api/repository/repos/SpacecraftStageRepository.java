package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.spacecraft.SpacecraftStageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for SpacecraftStageEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface SpacecraftStageRepository extends JpaRepository<SpacecraftStageEntity, Long> {
    
    // Custom query methods can be added here
    // Example: List<SpacecraftStageEntity> findByName(String name);
    // Example: Optional<SpacecraftStageEntity> findById(Long id);
    
}