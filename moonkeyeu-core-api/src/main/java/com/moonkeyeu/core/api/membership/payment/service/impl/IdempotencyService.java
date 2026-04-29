package com.moonkeyeu.core.api.membership.payment.service.impl;

import com.moonkeyeu.core.api.membership.domain.model.IdempotencyKey;
import com.moonkeyeu.core.api.membership.domain.repository.IdempotencyKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    public Optional<IdempotencyKey> getProcessedByIdempotencyKey(String idempotencyKey) {
        return idempotencyKeyRepository.findIdempotencyKeyByIdempotencyKey(idempotencyKey);
    }

    public void createIdempotencyKey(String idempotencyKey) {
        IdempotencyKey.builder()
                .idempotencyKey(idempotencyKey)
                .build();

    }
}
