package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.spacecraft.SpacecraftTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for SpacecraftTypeEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface SpacecraftTypeRepository extends JpaRepository<SpacecraftTypeEntity, Long> {
    
    // Custom query methods can be added here
    // Example: List<SpacecraftTypeEntity> findByName(String name);
    // Example: Optional<SpacecraftTypeEntity> findById(Long id);
    
}