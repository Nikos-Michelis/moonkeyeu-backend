package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.rocket.RocketConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for RocketConfigurationEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface RocketConfigurationRepository extends JpaRepository<RocketConfigurationEntity, Long> {
}