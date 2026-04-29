package com.moonkeyeu.core.api.membership.domain.repository;

import com.moonkeyeu.core.api.membership.domain.model.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {
    Optional<IdempotencyKey> findIdempotencyKeyByIdempotencyKey(String idempotencyKey);
}
