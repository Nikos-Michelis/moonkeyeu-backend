package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.spacecraft.SpacecraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for SpacecraftEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface SpacecraftRepository extends JpaRepository<SpacecraftEntity, Long> {
    
    // Custom query methods can be added here
    // Example: List<SpacecraftEntity> findByName(String name);
    // Example: Optional<SpacecraftEntity> findById(Long id);
    
}