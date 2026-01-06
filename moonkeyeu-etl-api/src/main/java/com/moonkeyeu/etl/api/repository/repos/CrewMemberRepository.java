package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.crew.CrewMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CrewMemberEntity
 * Provides CRUD operations and custom query methods
 */
@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMemberEntity, Long> {
}