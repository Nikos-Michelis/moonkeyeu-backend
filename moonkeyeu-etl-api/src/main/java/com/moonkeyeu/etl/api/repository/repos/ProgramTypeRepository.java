package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.programs.ProgramTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for ProgramTypeEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface ProgramTypeRepository extends JpaRepository<ProgramTypeEntity, Long> {}