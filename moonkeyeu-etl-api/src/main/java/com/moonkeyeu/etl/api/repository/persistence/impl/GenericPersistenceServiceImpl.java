package com.moonkeyeu.etl.api.repository.persistence.impl;

import com.moonkeyeu.etl.api.repository.persistence.GenericPersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Service
public class GenericPersistenceServiceImpl implements GenericPersistenceService {

    private final Repositories repositories;

    public GenericPersistenceServiceImpl(WebApplicationContext applicationContext) {
        this.repositories = new Repositories(applicationContext);
    }

    @Override
    public <T> T save(T object) {
        Object repo = repositories.getRepositoryFor(object.getClass())
                .orElseThrow(() -> new IllegalStateException("Can't find repository for entity of type " + object.getClass()));
        CrudRepository<T, Long> crudRepository = (CrudRepository<T, Long>) repo;
        return crudRepository.save(object);
    }

    @Override
    public <T> void saveAll(Chunk<?> entities) {
        if (entities.isEmpty()) return;

        Object first = entities.getItems().getFirst();
        Object repo = repositories.getRepositoryFor(first.getClass())
                .orElseThrow(() -> new IllegalStateException("Can't find repository for entity of type " + first.getClass()));
        CrudRepository<Object, Long> crudRepository = (CrudRepository<Object, Long>) repo;
        crudRepository.saveAll((List<Object>) entities.getItems());
    }
}
