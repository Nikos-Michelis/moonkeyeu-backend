package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.programs.ProgramTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for ProgramTypeEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface ProgramTypeRepository extends JpaRepository<ProgramTypeEntity, Long> {
    
    // Custom query methods can be added here
    // Example: List<ProgramTypeEntity> findByName(String name);
    // Example: Optional<ProgramTypeEntity> findById(Long id);
    
}