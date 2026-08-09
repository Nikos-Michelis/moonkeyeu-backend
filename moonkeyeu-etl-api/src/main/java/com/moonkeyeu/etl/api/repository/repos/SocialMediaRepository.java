package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.media.SocialMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for SocialMediaEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface SocialMediaRepository extends JpaRepository<SocialMediaEntity, Long> {
}