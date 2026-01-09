package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.launcher.LauncherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for LauncherEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface LauncherRepository extends JpaRepository<LauncherEntity, Long> { }