package com.moonkeyeu.core.api.launch.integration.repository;

import com.moonkeyeu.core.api.launch.config.TestContainerConfiguration;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.model.spacecraft.SpacecraftConfiguration;
import com.moonkeyeu.core.api.launch.repository.SpacecraftRepository;
import com.moonkeyeu.core.api.launch.repository.specifications.SpacecraftConfigSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
class SpacecraftRepositoryIT {
    @Autowired
    private SpacecraftRepository spacecraftRepository;
    private Specification<SpacecraftConfiguration> spec;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        PageSortingDTO testPageSortingDTO = PageSortingDTO.builder()
                .page(0)
                .limit(12)
                .field("maidenFlight")
                .sort("asc")
                .build();

        this.spec = SpacecraftConfigSpecification.hasSearchKey("Space Shuttle");
        this.pageable =
                PageRequest.of(
                        testPageSortingDTO.getPage(),
                        testPageSortingDTO.getLimit(),
                        Sort.by(testPageSortingDTO.getField()).ascending()
                );

    }

    @Test
    void ShouldFindSpacecraftConfigById() {
        Optional<SpacecraftConfiguration> program = spacecraftRepository.findSpacecraftWithSpacecraftId(6);
        assertTrue(program.isPresent());
    }

    @Test
    void shouldFindAllSpacecraftConfigs() {
        Page<SpacecraftConfiguration> programs = spacecraftRepository.findAll(this.spec, this.pageable);
        assertTrue(programs.getTotalElements() > 0);
    }
}