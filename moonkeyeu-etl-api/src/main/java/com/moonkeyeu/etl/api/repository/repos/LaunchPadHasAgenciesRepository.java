package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.pad.LaunchPadHasAgenciesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for LaunchPadHasAgenciesEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface LaunchPadHasAgenciesRepository extends JpaRepository<LaunchPadHasAgenciesEntity, Long> {}