package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for LocationEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {}