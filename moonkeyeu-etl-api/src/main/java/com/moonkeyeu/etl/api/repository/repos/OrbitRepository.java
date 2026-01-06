package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.mission.OrbitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for OrbitEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface OrbitRepository extends JpaRepository<OrbitEntity, Long> {
    
    // Custom query methods can be added here
    // Example: List<OrbitEntity> findByName(String name);
    // Example: Optional<OrbitEntity> findById(Long id);
    
}