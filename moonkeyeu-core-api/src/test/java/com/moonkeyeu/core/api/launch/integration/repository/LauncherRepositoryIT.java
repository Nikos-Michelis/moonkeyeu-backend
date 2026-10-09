package com.moonkeyeu.core.api.launch.integration.repository;

import com.moonkeyeu.core.api.launch.config.TestContainerConfiguration;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.model.launcher.Launcher;
import com.moonkeyeu.core.api.launch.repository.LauncherRepository;
import com.moonkeyeu.core.api.launch.repository.specifications.LauncherSpecification;
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
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
class LauncherRepositoryIT {
    @Autowired
    private LauncherRepository launcherRepository;
    private Specification<Launcher> spec;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        PageSortingDTO testPageSortingDTO = PageSortingDTO.builder()
                .page(0)
                .limit(12)
                .field("serialNumber")
                .sort("asc")
                .build();

        this.spec = LauncherSpecification.hasSearchKey("F1 B0001");
        this.pageable =
                PageRequest.of(
                        testPageSortingDTO.getPage(),
                        testPageSortingDTO.getLimit(),
                        Sort.by(testPageSortingDTO.getField()).ascending()
                );

    }

    /*@Test
    void findLauncherById() {
        Integer launcherId = 3;
        Optional<Launcher> launcher = launcherRepository.findLauncherWithLauncherId(launcherId);
        assertTrue(launcher.isPresent());
    }*/

    @Test
    void ShouldFindAllLaunchers() {
        Page<Launcher> launchers = launcherRepository.findAll(this.spec, this.pageable);
        assertTrue(launchers.getTotalElements() > 0);
    }
}