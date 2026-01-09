package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.agency.AgencyTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for AgencyTypeEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface AgencyTypeRepository extends JpaRepository<AgencyTypeEntity, Long> {}