package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.media.InfoUrlsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for InfoUrlsEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface InfoUrlsRepository extends JpaRepository<InfoUrlsEntity, Long> {
}