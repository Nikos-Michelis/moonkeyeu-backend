package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.launcher.LauncherStageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for LauncherStageEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface LauncherStageRepository extends JpaRepository<LauncherStageEntity, Long> {}