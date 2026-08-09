package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.media.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for VideoEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {}