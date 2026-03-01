package com.moonkeyeu.core.api.launch.integration.service.impl.search;

import com.moonkeyeu.core.api.launch.config.TestContainerConfig;
import com.moonkeyeu.core.api.launch.config.TestSecurityConfig;
import com.moonkeyeu.core.api.launch.dto.filters.FiltersDTO;
import com.moonkeyeu.core.api.launch.services.impl.search.FiltersServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@Import(TestSecurityConfig.class)
@Transactional
@DisplayName("FiltersServiceImplTest Integration Tests")
class FiltersServiceImplIntTest extends TestContainerConfig {
    @Autowired
    private FiltersServiceImpl filtersService;

    @Test
    @DisplayName("Should Return Launch Filters Grouped And Sorted Alphabetically")
    void shouldFilterAndSortLaunchFilters() {
        // When
        Map<String, Object> result = filtersService.getLaunchFilters();
        // Then
        Map<String, List<FiltersDTO>> data = (Map<String, List<FiltersDTO>>) result.get("data");
        List<FiltersDTO> agencies = data.get("agencies");

        assertTrue(agencies.stream()
                .anyMatch(agency ->
                        agency.getFilterName().equalsIgnoreCase("European Space Agency")),
                "European Space Agency");
        assertTrue(agencies.stream()
                .anyMatch(agency ->
                        agency.getFilterName().equalsIgnoreCase("National Aeronautics And Space Administration")),
                "National Aeronautics And Space Administration");
    }

    @Test
    @DisplayName("Should return Astronaut Filters Grouped And Sorted Alphabetically")
    void shouldFilterAndSortAstronautFilters() {
        // When
        Map<String, Object> result = filtersService.getAstronautFilters();
        // Then
        Map<String, List<FiltersDTO>> data = (Map<String, List<FiltersDTO>>) result.get("data");
        List<FiltersDTO> nationalities = data.get("nationalities");

        assertTrue(nationalities.stream()
                        .anyMatch(nation -> nation.getFilterName().equalsIgnoreCase("Canada")), "Canada");
        assertTrue(nationalities.stream()
                        .anyMatch(nation -> nation.getFilterName().equalsIgnoreCase("Greece")), "Greece");
    }
}