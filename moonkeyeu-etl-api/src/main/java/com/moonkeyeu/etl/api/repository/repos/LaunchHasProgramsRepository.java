package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.programs.LaunchHasProgramsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for LaunchHasProgramsEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface LaunchHasProgramsRepository extends JpaRepository<LaunchHasProgramsEntity, Long> {}