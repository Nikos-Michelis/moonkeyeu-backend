package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.spacecraft.SpacecraftConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for SpacecraftConfigurationEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface SpacecraftConfigurationRepository extends JpaRepository<SpacecraftConfigurationEntity, Long> {}