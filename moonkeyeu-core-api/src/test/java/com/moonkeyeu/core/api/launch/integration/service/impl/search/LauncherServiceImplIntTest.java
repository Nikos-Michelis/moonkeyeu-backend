package com.moonkeyeu.core.api.launch.integration.service.impl.search;

import com.moonkeyeu.core.api.launch.config.TestSecurityConfig;
import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.services.impl.search.LauncherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
@Import(TestSecurityConfig.class)
@Transactional
@DisplayName("LauncherServiceImplTest Integration Tests")
class LauncherServiceImplIntTest {

    @Autowired
    private LauncherServiceImpl launcherService;
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
        this.testRequestParams.put("search", "B1234");
    }

    @Nested
    @DisplayName("Search Launcher Stage By Filters")
    class SearchLauncherTest {

        @Test
        @DisplayName("Should Return Paged Launcher Stage When No Filters Are Applied")
        void shouldReturnPagedLauncherStageWithoutFilters() {
            Page<DTOEntity> result = launcherService.searchLauncher(Collections.emptyMap(), testPageSortingDTO);
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Should Return Paged Launcher Stage When Filters Are Applied")
        void shouldReturnPagedLauncherStageWithFilters() {
            Page<DTOEntity> result = launcherService.searchLauncher(testRequestParams, testPageSortingDTO);
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }
}