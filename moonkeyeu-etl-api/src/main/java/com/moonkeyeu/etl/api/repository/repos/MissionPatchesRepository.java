package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.media.MissionPatchesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for MissionPatchesEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface MissionPatchesRepository extends JpaRepository<MissionPatchesEntity, Long> {
    
    // Custom query methods can be added here
    // Example: List<MissionPatchesEntity> findByName(String name);
    // Example: Optional<MissionPatchesEntity> findById(Long id);
    
}