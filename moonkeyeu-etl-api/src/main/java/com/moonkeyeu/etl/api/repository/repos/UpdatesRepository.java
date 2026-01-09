package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.media.UpdatesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for UpdatesEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface UpdatesRepository extends JpaRepository<UpdatesEntity, Long> {
    
    // Custom query methods can be added here
    // Example: List<UpdatesEntity> findByName(String name);
    // Example: Optional<UpdatesEntity> findById(Long id);
    
}