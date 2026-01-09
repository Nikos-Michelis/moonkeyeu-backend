package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.mission.MissionHasAgenciesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for MissionHasAgenciesEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface MissionHasAgenciesRepository extends JpaRepository<MissionHasAgenciesEntity, Long> {
    
    // Custom query methods can be added here
    // Example: List<MissionHasAgenciesEntity> findByName(String name);
    // Example: Optional<MissionHasAgenciesEntity> findById(Long id);
    
}