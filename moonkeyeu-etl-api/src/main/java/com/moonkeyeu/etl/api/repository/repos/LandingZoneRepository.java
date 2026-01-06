package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.landing.LandingZoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for LandingZoneEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface LandingZoneRepository extends JpaRepository<LandingZoneEntity, Long> { }