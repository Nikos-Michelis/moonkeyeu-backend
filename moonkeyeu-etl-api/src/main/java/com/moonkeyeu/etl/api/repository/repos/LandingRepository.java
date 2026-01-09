package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.landing.LandingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for LandingEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface LandingRepository extends JpaRepository<LandingEntity, Long> {}