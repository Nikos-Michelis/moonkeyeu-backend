package com.moonkeyeu.core.api.launch.unit.services.impl.search;

import com.moonkeyeu.core.api.launch.services.impl.search.FiltersServiceImpl;
import com.moonkeyeu.core.api.utils.mapper.DtoConverter;
import com.moonkeyeu.core.api.launch.dto.filters.FiltersDTO;
import com.moonkeyeu.core.api.launch.model.views.BaseFilter;
import com.moonkeyeu.core.api.launch.repository.filters.FiltersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FiltersServiceImplTest Unit Tests")
class FiltersServiceImplTest {
    @Mock
    private FiltersRepository filtersRepository;
    @Mock
    private DtoConverter dtoConverter;
    @InjectMocks
    private FiltersServiceImpl filtersService;
    private List<BaseFilter> launchRepositoryData;
    private List<BaseFilter> astronautRepositoryData;

    @BeforeEach
    void setup() {
        BaseFilter nasa = BaseFilter.builder()
                .filterId(1L)
                .filterName("NASA")
                .filterType("agencies")
                .build();

        BaseFilter esa = BaseFilter.builder()
                .filterId(2L)
                .filterName("ESA")
                .filterType("agencies")
                .build();

        BaseFilter location = BaseFilter.builder()
                .filterId(3L)
                .filterName("Starbase")
                .filterType("locations")
                .build();

        this.launchRepositoryData = List.of(nasa, esa, location);

        BaseFilter statusActive = BaseFilter.builder()
                .filterId(1L)
                .filterName("Active")
                .filterType("astronaut_status")
                .build();

        BaseFilter natGreece = BaseFilter.builder()
                .filterId(2L)
                .filterName("Greece")
                .filterType("nationality")
                .build();

        BaseFilter natAmerican = BaseFilter.builder()
                .filterId(3L)
                .filterName("American")
                .filterType("nationality")
                .build();

        this.astronautRepositoryData = List.of(natGreece, natAmerican, statusActive);
    }

    @Test
    @DisplayName("Should Return Launch Filters Grouped And Sorted Alphabetically")
    void shouldFilterAndSortLaunchFilters() {
        // Given
        when(filtersRepository.findAllLaunchFilters())
                .thenReturn(this.launchRepositoryData);

        when(dtoConverter.convertToDto(any(), eq(FiltersDTO.class)))
                .thenAnswer(invocation -> {
                    BaseFilter source = invocation.getArgument(0);
                    return new FiltersDTO(source.getFilterId(), source.getFilterName());
                });

        // When
        Map<String, Object> result = filtersService.getLaunchFilters();

        // Then
        Map<String, List<FiltersDTO>> data =
                (Map<String, List<FiltersDTO>>) result.get("data");

        List<FiltersDTO> agencies = data.get("agencies");
        assertEquals(2, agencies.size());

        assertEquals("ESA", agencies.get(0).getFilterName());
        assertEquals("NASA", agencies.get(1).getFilterName());

        verify(filtersRepository, times(1))
                .findAllLaunchFilters();

        verify(dtoConverter, times(3))
                .convertToDto(any(), eq(FiltersDTO.class));
    }

     @Test
     @DisplayName("Should return Astronaut Filters Grouped And Sorted Alphabetically")
     void shouldFilterAndSortAstronautFilters() {
        // Given
        when(filtersRepository.findAllAstronautFilters())
                .thenReturn(this.astronautRepositoryData);

        when(dtoConverter.convertToDto(any(), eq(FiltersDTO.class)))
                .thenAnswer(invocation -> {
                    BaseFilter source = invocation.getArgument(0);
                    return new FiltersDTO(source.getFilterId(), source.getFilterName());
                });

        // When
        Map<String, Object> result = filtersService.getAstronautFilters();

        // Then
        Map<String, List<FiltersDTO>> data =
                (Map<String, List<FiltersDTO>>) result.get("data");

        List<FiltersDTO> nationalities = data.get("nationalities");

        assertEquals(2, nationalities.size());

        assertEquals("American", nationalities.get(0).getFilterName());
        assertEquals("Greece", nationalities.get(1).getFilterName());

        verify(filtersRepository, times(1))
                .findAllAstronautFilters();

        verify(dtoConverter, times(3))
                .convertToDto(any(), eq(FiltersDTO.class));
     }
}