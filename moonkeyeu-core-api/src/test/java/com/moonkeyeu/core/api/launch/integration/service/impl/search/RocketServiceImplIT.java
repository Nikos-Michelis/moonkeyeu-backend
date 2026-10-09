package com.moonkeyeu.core.api.launch.integration.service.impl.search;

import com.moonkeyeu.core.api.launch.config.TestContainerConfiguration;
import com.moonkeyeu.core.api.launch.config.TestSecurityConfiguration;
import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.dto.rocket.RocketNormalDTO;
import com.moonkeyeu.core.api.launch.services.impl.search.RocketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
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
@Import({TestSecurityConfiguration.class, TestContainerConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("RocketServiceImplIntTest Integration Tests")
@Transactional
class RocketServiceImplIT {
    @Autowired
    private RocketServiceImpl rocketService;
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
    @DisplayName("Search Rocket, Rocket Configuration by Filters")
    class SearchRocketTests {

        @Test
        @DisplayName("Should Return Paged Rocket Configuration Summaries When No Filters Are Applied")
        void shouldReturnPagedRocketConfigsWithoutFilters() {
            // when
            Page<DTOEntity> result = rocketService.searchRocket(Collections.emptyMap(), testPageSortingDTO);
            // then
            assertNotNull(result);
            assertEquals(testPageSortingDTO.getLimit(), result.getPageable().getPageSize());
        }

        @Test
        @DisplayName("Should Return Paged Rocket Configuration Summaries When Filters Are Applied")
        void shouldReturnPagedRocketsWithFilters() {
            // when
            Page<DTOEntity> result = rocketService.searchRocket(testRequestParams, testPageSortingDTO);
            // then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Find Rocket By ID")
    class FindRocketById {
        @Test
        @DisplayName("Should return a Rocket When ID Exists")
        void shouldReturnRocketById() {
            // given
            final Integer rocketId = 453;
            // when
            RocketNormalDTO result = rocketService.getRocketById(rocketId);
            // then
            assertNotNull(result);
            assertEquals(rocketId, result.getRocketId().intValue());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException When Rocket ID Is Null")
        void shouldHandleNullRocketId() {
            // given
            final Integer rocketId = null;
            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> rocketService.getRocketById(rocketId));

            assertNotNull(exception);
            assertEquals("Rocket not found with id: " + rocketId, exception.getMessage());
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Rocket ID Not Found")
        void shouldThrowResourceNotFoundException() {
            // given
            final Integer rocketId = 123456;

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> rocketService.getRocketById(rocketId));

            assertNotNull(exception);
            assertEquals("Rocket not found with id: " + rocketId, exception.getMessage());
        }
    }
}