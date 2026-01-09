package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.launcher.LauncherStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for LauncherStatusEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface LauncherStatusRepository extends JpaRepository<LauncherStatusEntity, Long> { }