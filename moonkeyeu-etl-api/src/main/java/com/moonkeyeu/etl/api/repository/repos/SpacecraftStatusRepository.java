package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.spacecraft.SpacecraftStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for SpacecraftStatusEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface SpacecraftStatusRepository extends JpaRepository<SpacecraftStatusEntity, Long> {
    
    // Custom query methods can be added here
    // Example: List<SpacecraftStatusEntity> findByName(String name);
    // Example: Optional<SpacecraftStatusEntity> findById(Long id);
    
}