package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.country.AgenciesHasCountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for AgenciesHasCountryEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface AgenciesHasCountryRepository extends JpaRepository<AgenciesHasCountryEntity, Long> { }