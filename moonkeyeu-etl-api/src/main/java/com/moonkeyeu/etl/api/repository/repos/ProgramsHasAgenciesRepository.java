package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.programs.ProgramsHasAgenciesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for ProgramsHasAgenciesEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface ProgramsHasAgenciesRepository extends JpaRepository<ProgramsHasAgenciesEntity, Long> {
    
    // Custom query methods can be added here
    // Example: List<ProgramsHasAgenciesEntity> findByName(String name);
    // Example: Optional<ProgramsHasAgenciesEntity> findById(Long id);
    
}