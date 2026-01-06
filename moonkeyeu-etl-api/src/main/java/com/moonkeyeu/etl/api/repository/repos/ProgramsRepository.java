package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.programs.ProgramsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for ProgramsEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface ProgramsRepository extends JpaRepository<ProgramsEntity, Long> {
    
    // Custom query methods can be added here
    // Example: List<ProgramsEntity> findByName(String name);
    // Example: Optional<ProgramsEntity> findById(Long id);
    
}