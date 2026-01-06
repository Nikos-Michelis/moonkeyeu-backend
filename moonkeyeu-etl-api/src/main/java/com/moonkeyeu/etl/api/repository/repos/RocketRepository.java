package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.rocket.RocketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for RocketEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface RocketRepository extends JpaRepository<RocketEntity, Long> {
    
    // Custom query methods can be added here
    // Example: List<RocketEntity> findByName(String name);
    // Example: Optional<RocketEntity> findById(Long id);
    
}