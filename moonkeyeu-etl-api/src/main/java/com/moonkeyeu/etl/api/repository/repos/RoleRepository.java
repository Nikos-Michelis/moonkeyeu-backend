package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.crew.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for RoleEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {}