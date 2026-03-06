package com.moonkeyeu.core.api.launch.integration.repository;

import com.moonkeyeu.core.api.launch.config.TestContainerConfiguration;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.model.astronaut.Astronaut;
import com.moonkeyeu.core.api.launch.repository.AstronautsRepository;
import com.moonkeyeu.core.api.launch.repository.specifications.AstronautSpecification;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
class AstronautsRepositoryIT {
    @Autowired
    private AstronautsRepository astronautsRepository;
    private Specification<Astronaut> spec;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        PageSortingDTO testPageSortingDTO = PageSortingDTO.builder()
                .page(0)
                .limit(12)
                .field("name")
                .sort("asc")
                .build();

        this.spec = AstronautSpecification.hasSearchKey("Yury Usachov");
        this.spec.and(AstronautSpecification.hasNationality("5"));
        this.spec.and(AstronautSpecification.hasStatus("2"));
        this.pageable =
                PageRequest.of(
                        testPageSortingDTO.getPage(),
                        testPageSortingDTO.getLimit(),
                        Sort.by(testPageSortingDTO.getField()).ascending()
                );

    }

    @Test
    void findAstronautById() {
        Integer astronautId = 274;
        Optional<Astronaut> astronaut = astronautsRepository.findAstronautByAstronautId(astronautId);
        assertTrue(astronaut.isPresent());
    }

    @Test
    void ShouldFindAllAstronauts() {
        Page<Astronaut> astronauts = astronautsRepository.findAll(this.spec, this.pageable);
        assertEquals(1, astronauts.getTotalElements());
    }
}