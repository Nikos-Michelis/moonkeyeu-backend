package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.landing.LandingTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for LandingTypeEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface LandingTypeRepository extends JpaRepository<LandingTypeEntity, Long> { }