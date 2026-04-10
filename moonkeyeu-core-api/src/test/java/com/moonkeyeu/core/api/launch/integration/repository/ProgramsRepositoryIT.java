package com.moonkeyeu.core.api.launch.integration.repository;

import com.moonkeyeu.core.api.launch.config.TestContainerConfiguration;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.model.program.Programs;
import com.moonkeyeu.core.api.launch.repository.ProgramsRepository;
import com.moonkeyeu.core.api.launch.repository.specifications.ProgramSpecification;
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
class ProgramsRepositoryIT {
    @Autowired
    private ProgramsRepository programsRepository;
    private Specification<Programs> spec;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        PageSortingDTO testPageSortingDTO = PageSortingDTO.builder()
                .page(0)
                .limit(12)
                .field("startDate")
                .sort("asc")
                .build();

        this.spec = ProgramSpecification.hasSearchKey("Space Shuttle");
        this.pageable =
                PageRequest.of(
                        testPageSortingDTO.getPage(),
                        testPageSortingDTO.getLimit(),
                        Sort.by(testPageSortingDTO.getField()).ascending()
                );

    }

    @Test
    void ShouldFindProgramById() {
        Optional<Programs> program = programsRepository.findProgramById(6);
        assertTrue(program.isPresent());
    }

    @Test
    void ShouldFindAllPrograms() {
        Page<Programs> programs = programsRepository.findAll(this.spec, this.pageable);
        assertTrue(programs.getTotalElements() > 0);
    }
}