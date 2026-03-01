package com.moonkeyeu.core.api.launch.integration.service.impl.search;

import com.moonkeyeu.core.api.launch.config.TestContainerConfig;
import com.moonkeyeu.core.api.launch.config.TestSecurityConfig;
import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.launch.LaunchDTO;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.services.impl.search.LaunchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
@Import(TestSecurityConfig.class)
@Transactional
@DisplayName("LaunchServiceImplIntTest Integration Tests")
class LaunchServiceImplIntTest extends TestContainerConfig {
    @Autowired
    private LaunchServiceImpl launchService;
    private PageSortingDTO testPageSortingDTO;
    private Map<String, String> testRequestParams;

    @BeforeEach
    void setUp() {
        this.testPageSortingDTO = PageSortingDTO.builder()
                .page(0)
                .limit(12)
                .field("net")
                .sort("asc")
                .build();

        this.testRequestParams = new HashMap<>();
        this.testRequestParams.put("upcoming", "false");
        this.testRequestParams.put("agency", "191");
        this.testRequestParams.put("program", "6");
        this.testRequestParams.put("pad", "87");
        this.testRequestParams.put("rocketConfig", "453");
    }

    @Nested
    @DisplayName("Search Launch by Filters")
    class searchLaunchByParamsTests {

        @Test
        @DisplayName("Should Return Paged Launch Normal Summaries When No Filters Are Applied")
        void shouldReturnPagedLaunchesWithoutFilters() {
            Page<DTOEntity> result = launchService.searchLaunch(Collections.emptyMap(), testPageSortingDTO);
            assertNotNull(result);
            assertEquals(testPageSortingDTO.getLimit(), result.getPageable().getPageSize());
        }

        @Test
        @DisplayName("Should Return Paged Launch Normal Summaries When Filters Are Applied")
        void shouldReturnPagedLaunchesWithFilters() {
            Page<DTOEntity> result = launchService.searchLaunch(testRequestParams, testPageSortingDTO);
            assertNotNull(result);
            assertTrue(result.getTotalElements() >= 10);
        }
    }

    @Nested
    @DisplayName("Find Launch By ID")
    class findLaunchById {
        @Test
        @DisplayName("Should Return a Launch When ID Exists")
        void shouldReturnLaunchById() {
            //given
            final String launchId = "bf08a10b-35f0-4736-97f3-ba111e59cd55";
            // when
            LaunchDTO result = (LaunchDTO) launchService.getLaunchById(launchId);
            // then
            assertNotNull(result);
            assertEquals(launchId, result.getLaunchId());
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Launch ID Is Null")
        void shouldHandleNullLaunchId() {
            // given
            final String launchId = null;
            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> launchService.getLaunchById(launchId));

            assertNotNull(exception);
            assertEquals("Launch not found with id: " + launchId, exception.getMessage());
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Launch Configuration ID Not Found")
        void shouldThrowResourceNotFoundException() {

            // given
            final String launchId = "test_267f232e-f34a-4c79-ac56-b00cf69dd32d";

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> launchService.getLaunchById(launchId));

            assertNotNull(exception);
            assertEquals("Launch not found with id: " + launchId, exception.getMessage());
        }
    }
}