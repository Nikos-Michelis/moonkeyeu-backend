package com.moonkeyeu.core.api.launch.integration.repository;

import com.moonkeyeu.core.api.launch.config.TestContainerConfig;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.model.launch.Launch;
import com.moonkeyeu.core.api.launch.repository.LaunchRepository;
import com.moonkeyeu.core.api.launch.repository.specifications.LaunchSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
class LaunchRepositoryTest extends TestContainerConfig {
    @Autowired
    private LaunchRepository launchRepository;
    private Specification<Launch> spec;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        PageSortingDTO testPageSortingDTO = PageSortingDTO.builder()
                .page(0)
                .limit(12)
                .field("net")
                .sort("asc")
                .build();
        this.spec = LaunchSpecification.rootSpecification();
        this.spec.and(LaunchSpecification.isUpcomingLaunch(false));
        this.spec.and(LaunchSpecification.hasAgency("44"));
        this.spec.and(LaunchSpecification.hasProgram("6"));
        this.spec.and(LaunchSpecification.hasLocation("87"));
        this.pageable =
                PageRequest.of(
                        testPageSortingDTO.getPage(),
                        testPageSortingDTO.getLimit(),
                        Sort.by(testPageSortingDTO.getField()).ascending()
                );

    }

    @Test
    void findLaunchWithLaunchId() {
        Optional<Launch> launch = launchRepository.findLaunchWithLaunchId("00441721-5019-4c49-aa85-e38aad2d3937");
        assertTrue(launch.isPresent());
    }

    @Test
    void findLaunchWithBookmarkIdAndLaunchId() {
        Optional<Launch> launch = launchRepository.findLaunchWithBookmarkIdAndLaunchId(1L,"00441721-5019-4c49-aa85-e38aad2d3937");
        assertFalse(launch.isPresent());
    }

    @Test
    void findUpcomingLaunchesByAgencyId() {
        Integer nasa = 44;
        Optional<Launch> launch = launchRepository.findUpcomingLaunchesByAgencyId(nasa);
        assertFalse(launch.isPresent());
    }

    @Test
    void findUpcomingLaunchesByProgramId() {
        Integer spaceShuttle = 6;
        Optional<Launch> launch = launchRepository.findUpcomingLaunchesByProgramId(spaceShuttle);
        assertFalse(launch.isPresent());
    }

    @Test
    void findUpcomingLaunchesByLaunchPadId() {
        Integer location = 87;
        Optional<Launch> launch = launchRepository.findUpcomingLaunchesByLaunchPadId(location);
        assertFalse(launch.isPresent());
    }
    @Test
    void findAll() {
        Page<Launch> launch = launchRepository.findAll(this.spec, this.pageable);
        assertTrue(launch.getTotalElements() > 0);
    }

}