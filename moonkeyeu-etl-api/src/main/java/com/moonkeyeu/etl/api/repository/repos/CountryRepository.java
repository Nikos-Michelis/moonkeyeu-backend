package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.country.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CountryEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, Long> {}