package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.launch.LaunchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for LaunchEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface LaunchRepository extends JpaRepository<LaunchEntity, String> {}