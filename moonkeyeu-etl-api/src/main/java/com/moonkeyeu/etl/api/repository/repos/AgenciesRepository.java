package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.agency.AgenciesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for AgenciesEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface AgenciesRepository extends JpaRepository<AgenciesEntity, Long> { }