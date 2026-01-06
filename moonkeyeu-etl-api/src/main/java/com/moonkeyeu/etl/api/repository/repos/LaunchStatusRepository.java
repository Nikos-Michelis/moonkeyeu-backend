package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.launch.LaunchStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for LaunchStatusEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface LaunchStatusRepository extends JpaRepository<LaunchStatusEntity, Long> {
    
    // Custom query methods can be added here
    // Example: List<LaunchStatusEntity> findByName(String name);
    // Example: Optional<LaunchStatusEntity> findById(Long id);
    
}