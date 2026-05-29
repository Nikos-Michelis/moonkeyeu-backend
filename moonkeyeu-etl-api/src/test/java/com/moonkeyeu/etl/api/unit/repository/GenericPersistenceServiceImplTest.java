package com.moonkeyeu.etl.api.unit.repository;

import com.moonkeyeu.etl.api.repository.persistence.impl.GenericPersistenceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenericPersistenceServiceImplTest {
    @Mock
    private WebApplicationContext applicationContext;
    @Mock
    private Repositories repositories;
    @Mock
    private CrudRepository<TestEntity, Long> crudRepository;
    @InjectMocks
    private GenericPersistenceServiceImpl genericPersistenceService;
    private TestEntity testEntity;

    @BeforeEach
    void setUp() {
        testEntity = new TestEntity();
    }

    @Test
    @DisplayName("Should save entity successfully")
    void save_shouldSaveEntity() {

        when(repositories.getRepositoryFor(TestEntity.class))
                .thenReturn(Optional.of(crudRepository));
        when(crudRepository.save(testEntity))
                .thenReturn(testEntity);

        TestEntity result = genericPersistenceService.save(testEntity);

        assertThat(result).isEqualTo(testEntity);
        verify(crudRepository).save(testEntity);
    }

    @Test
    @DisplayName("Should throw exception when repository not found for save")
    void save_shouldThrowException_whenRepositoryMissing() {

        when(repositories.getRepositoryFor(TestEntity.class))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> genericPersistenceService.save(testEntity))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Can't find repository");
    }


    @Test
    @DisplayName("Should save all entities in chunk")
    void saveAll_shouldSaveAllEntities() {

        Chunk<Object> chunk = new Chunk<>(List.of(testEntity));

        when(repositories.getRepositoryFor(TestEntity.class))
                .thenReturn(Optional.of(crudRepository));

        genericPersistenceService.saveAll(chunk);

        verify(crudRepository).saveAll(List.of(testEntity));
    }

    @Test
    @DisplayName("Should skip saveAll when chunk is empty")
    void saveAll_shouldSkip_whenChunkEmpty() {

        Chunk<Object> chunk = new Chunk<>(List.of());

        genericPersistenceService.saveAll(chunk);

        verifyNoInteractions(repositories);
    }

    @Test
    @DisplayName("Should throw exception when repository missing in saveAll")
    void saveAll_shouldThrowException_whenRepositoryMissing() {

        Chunk<Object> chunk = new Chunk<>(List.of(testEntity));

        when(repositories.getRepositoryFor(TestEntity.class))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> genericPersistenceService.saveAll(chunk))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Can't find repository");
    }

    static class TestEntity {
    }
}