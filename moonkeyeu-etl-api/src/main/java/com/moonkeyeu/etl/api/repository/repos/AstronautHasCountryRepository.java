package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.country.AstronautHasCountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for AstronautHasCountryEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface AstronautHasCountryRepository extends JpaRepository<AstronautHasCountryEntity, Long> {}