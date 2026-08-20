package com.moonkeyeu.etl.api.repository.persistence.impl;

import com.moonkeyeu.etl.api.repository.persistence.GenericPersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GenericPersistenceServiceImpl implements GenericPersistenceService {

    private final Repositories repositories;

    public GenericPersistenceServiceImpl(Repositories repositories) {
        this.repositories = repositories;
    }

    @Override
    public <T> T save(T object) {
        Object repository = repositories.getRepositoryFor(object.getClass())
                .orElseThrow(() -> new IllegalStateException("Can't find repository for entity of type " + object.getClass()));
        JpaRepository<T, Long> jpaRepository = (JpaRepository<T, Long>) repository;
        return jpaRepository.save(object);
    }

    @Override
    public <T> void saveAll(Chunk<T> entities) {
        if (entities.isEmpty()) return;

        Object first = entities.getItems().getFirst();
        Object repository = repositories.getRepositoryFor(first.getClass())
                .orElseThrow(() -> new IllegalStateException("Can't find repository for entity of type " + first.getClass()));
        JpaRepository<T, Long> jpaRepository = (JpaRepository<T, Long>) repository;
        jpaRepository.saveAll(entities.getItems());
    }
}
