package com.moonkeyeu.core.api.launch.integration.repository;

import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.model.launch.Launch;
import com.moonkeyeu.core.api.launch.model.launcher.Launcher;
import com.moonkeyeu.core.api.launch.model.program.Programs;
import com.moonkeyeu.core.api.launch.repository.LauncherRepository;
import com.moonkeyeu.core.api.launch.repository.ProgramsRepository;
import com.moonkeyeu.core.api.launch.repository.specifications.LauncherSpecification;
import com.moonkeyeu.core.api.launch.repository.specifications.ProgramSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LauncherRepositoryTest extends AbstractRepositoryTest{
    @Autowired
    private LauncherRepository launcherRepository;
    private Specification<Launcher> spec;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        PageSortingDTO testPageSortingDTO = PageSortingDTO.builder()
                .page(0)
                .limit(12)
                .field("startDate")
                .sort("asc")
                .build();

        this.spec = LauncherSpecification.hasSearchKey("Space Shuttle");
        this.pageable =
                PageRequest.of(
                        testPageSortingDTO.getPage(),
                        testPageSortingDTO.getLimit(),
                        Sort.by(testPageSortingDTO.getField()).ascending()
                );

    }

    @Test
    void findLauncherById() {
        Optional<Launcher> launcher = launcherRepository.findLauncherWithLauncherId(6);
        assertTrue(launcher.isPresent());
    }

    @Test
    void ShouldFindAllLaunchers() {
        Page<Launcher> launchers = launcherRepository.findAll(this.spec, this.pageable);
        assertTrue(launchers.getTotalElements() > 0);
    }
}