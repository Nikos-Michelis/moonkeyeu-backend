package com.moonkeyeu.core.api.launch.integration.service.impl.search;

import com.moonkeyeu.core.api.launch.config.TestContainerConfig;
import com.moonkeyeu.core.api.launch.config.TestSecurityConfig;
import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.astronaut.AstronautDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.services.impl.search.AstronautServiceImpl;
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
@DisplayName("AstronautServiceImpl Integration Tests")
class AstronautServiceImplIntTest extends TestContainerConfig {
    @Autowired
    private AstronautServiceImpl astronautService;
    private PageSortingDTO testPageSortingDTO;
    private Map<String, String> testRequestParams;

    @BeforeEach
    void setUp() {
        this.testPageSortingDTO = PageSortingDTO.builder()
                .page(0)
                .limit(12)
                .field("name")
                .sort("asc")
                .build();

        this.testRequestParams = new HashMap<>();
        this.testRequestParams.put("nationality", "5");
        this.testRequestParams.put("status", "2");
        this.testRequestParams.put("search", "Yury Usachov");
    }

    @Nested
    @DisplayName("Search Astronaut By Filters")
    class SearchAstronautTest {

        @Test
        @DisplayName("Should Return Paged Astronaut Normal Summaries When No Filters Are Applied")
        void shouldReturnPagedAstronautsWithoutFilters() {
            Page<DTOEntity> result = astronautService.searchAstronaut(Collections.emptyMap(), testPageSortingDTO);
            assertNotNull(result);
            assertEquals(testPageSortingDTO.getLimit(), result.getPageable().getPageSize());
        }


        @Test
        @DisplayName("Should Return Paged Astronaut Normal Summaries When Filters Are Applied")
        void shouldReturnPagedLaunchesWithFilters() {
            Page<DTOEntity> result =
                    astronautService.searchAstronaut(testRequestParams, testPageSortingDTO);
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Find Astronaut By ID")
    class FindAstronautById {
        @Test
        @DisplayName("Should Return a Astronaut When ID Exists")
        void shouldReturnLaunchById() {
            // given
            Integer astronautId = 274;
            // when
            AstronautDetailedDTO result = (AstronautDetailedDTO) astronautService.getAstronautById(astronautId);
            // then
            assertNotNull(result);
            assertEquals(astronautId, result.getAstronautId().intValue());
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Astronaut ID Is Null")
        void shouldHandleNullAstronautId() {
            // given
            final Integer astronautId = null;
            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> astronautService.getAstronautById(astronautId));

            assertNotNull(exception);
            assertEquals("Astronaut not found with id: " + astronautId, exception.getMessage());
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Astronaut ID Not Found")
        void shouldThrowResourceNotFoundException() {
            // given
            final Integer astronautId = 12356;
            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> astronautService.getAstronautById(astronautId));

            assertNotNull(exception);
            assertEquals("Astronaut not found with id: " + astronautId, exception.getMessage());
        }
    }
}