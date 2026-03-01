package com.moonkeyeu.core.api.launch.integration.service.impl.search;

import com.moonkeyeu.core.api.launch.config.TestContainerConfig;
import com.moonkeyeu.core.api.launch.config.TestSecurityConfig;
import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.dto.spacecraft.SpacecraftConfigurationDTO;
import com.moonkeyeu.core.api.launch.services.impl.search.SpacecraftServiceImpl;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
@Import(TestSecurityConfig.class)
@Transactional
@DisplayName("SpacecraftServiceImplIntTest Integration Tests")
class SpacecraftServiceImplIntTest extends TestContainerConfig {
    @Autowired
    private SpacecraftServiceImpl spacecraftService;
    private PageSortingDTO testPageSortingDTO;
    private Map<String, String> testRequestParams;

    @BeforeEach
    void setUp() {
        this.testPageSortingDTO = PageSortingDTO.builder()
                .page(0)
                .limit(12)
                .field("maidenFlight")
                .sort("asc")
                .build();

        this.testRequestParams = new HashMap<>();
        this.testRequestParams.put("search", "Space Shuttle");
    }

    @Nested
    @DisplayName("Search Spacecraft Configurations By Filters")
    class SearchSpacecraftConfigsTests {

        @Test
        @DisplayName("Should Return Paged Spacecraft Configuration Summaries When No Filters Are Applied")
        void shouldReturnPagedSpacecraftConfigsWithoutFilters() {
            // when
            Page<DTOEntity> result = spacecraftService.searchSpacecraft(Collections.emptyMap(), testPageSortingDTO);
            // then
            assertNotNull(result);
            assertEquals(testPageSortingDTO.getLimit(), result.getPageable().getPageSize());
        }

        @Test
        @DisplayName("Should Return Paged Spacecraft Configuration Summaries When Filters Are Applied")
        void shouldReturnPagedSpacecraftConfigsWithFilters() {
            // when
            Page<DTOEntity> result =
                    spacecraftService.searchSpacecraft(testRequestParams, testPageSortingDTO);

            // then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Find SpacecraftConfig By ID")
    class FindSpacecraftConfigById {
        @Test
        @DisplayName("Should Return a Spacecraft Configuration When ID Exists")
        void shouldReturnSpacecraftConfigById() {
            // given
            final Integer spacecraftConfigId = 14;

            // when
            SpacecraftConfigurationDTO result = spacecraftService.getSpacecraftById(spacecraftConfigId);

            // then
            assertNotNull(result);
            assertEquals(spacecraftConfigId, result.getSpacecraftConfId());
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Spacecraft Configuration ID Is Null")
        void shouldHandleNullSpacecraftConfigId() {
            // given
            final Integer spacecraftConfigId = null;

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> spacecraftService.getSpacecraftById(spacecraftConfigId));

            assertNotNull(exception);
            assertEquals("Spacecraft configuration not found with id: " + spacecraftConfigId, exception.getMessage());
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Spacecraft Configuration ID Not Found")
        void shouldThrowResourceNotFoundException() {
            // given
            final Integer spacecraftConfigId = 123456;
            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> spacecraftService.getSpacecraftById(spacecraftConfigId));

            assertNotNull(exception);
            assertEquals("Spacecraft configuration not found with id: " + spacecraftConfigId, exception.getMessage());
        }
    }
}