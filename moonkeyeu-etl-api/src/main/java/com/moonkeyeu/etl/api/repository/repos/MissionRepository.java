package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.mission.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for MissionEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface MissionRepository extends JpaRepository<MissionEntity, Long> {
    
    // Custom query methods can be added here
    // Example: List<MissionEntity> findByName(String name);
    // Example: Optional<MissionEntity> findById(Long id);
    
}