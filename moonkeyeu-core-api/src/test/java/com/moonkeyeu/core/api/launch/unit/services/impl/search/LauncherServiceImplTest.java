package com.moonkeyeu.core.api.launch.unit.services.impl.search;

import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.launcher.LauncherDTO;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.model.launcher.Launcher;
import com.moonkeyeu.core.api.launch.repository.LauncherRepository;
import com.moonkeyeu.core.api.launch.services.impl.search.LauncherServiceImpl;
import com.moonkeyeu.core.api.utils.mapper.DtoConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("LauncherServiceImplTest Unit Tests")
class LauncherServiceImplTest {
    @Mock
    private LauncherRepository launcherRepository;
    @Mock
    private DtoConverter dtoConverter;
    @InjectMocks
    private LauncherServiceImpl launcherService;
    private PageSortingDTO testPageSortingDTO;
    private Map<String, String> testRequestParams;
    private Launcher testLauncher;
    private LauncherDTO testLauncherDTO;
    
    @BeforeEach
    void setUp() {
        this.testPageSortingDTO = PageSortingDTO.builder()
                .page(1)
                .limit(12)
                .field("name")
                .sort("desc")
                .build();

        this.testRequestParams = new HashMap<>();
        this.testRequestParams.put("search", "B1101");

        this.testLauncher = new Launcher();
        this.testLauncher.setLauncherId(32L);
        this.testLauncher.setSerialNumber("B1101");
        this.testLauncher.setFlightProven(true);

        this.testLauncherDTO = new LauncherDTO();
        this.testLauncherDTO.setLauncherId(testLauncher.getLauncherId());
        this.testLauncherDTO.setSerialNumber(testLauncher.getSerialNumber());
        this.testLauncherDTO.setFlightProven(testLauncher.getFlightProven());
    }

    @Nested
    @DisplayName("Search Launcher Stage By Filters")
    class SearchLauncherTest {

        @Test
        @DisplayName("Should Return Paged Launcher Stage When No Filters Are Applied")
        void shouldReturnPagedLauncherStageWithoutFilters() {

            // given: service method going to test
            Page<Launcher> launcherPage = new PageImpl<>(List.of(testLauncher));

            when(launcherRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(launcherPage);

            when(dtoConverter.convertToDto(eq(testLauncher), eq(LauncherDTO.class)))
                    .thenReturn(testLauncherDTO);

            // when: when test runs
            Page<DTOEntity> result =
                    launcherService.searchLauncher(null, testPageSortingDTO);

            // then: result of test
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(testLauncherDTO, result.getContent().get(0));

            verify(launcherRepository, times(1))
                    .findAll(any(Specification.class), any(Pageable.class));

            verify(dtoConverter, times(result.getSize()))
                    .convertToDto(testLauncher, LauncherDTO.class);
        }

        @Test
        @DisplayName("Should Return Paged Launcher Stage When Filters Are Applied")
        void shouldReturnPagedLauncherStageWithFilters() {

            // given: service method going to test
            Page<Launcher> launcherPage = new PageImpl<>(List.of(testLauncher));

            when(launcherRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(launcherPage);

            when(dtoConverter.convertToDto(eq(testLauncher), eq(LauncherDTO.class)))
                    .thenReturn(testLauncherDTO);

            // when: when test runs
            Page<DTOEntity> result =
                    launcherService.searchLauncher(testRequestParams, testPageSortingDTO);

            // then: result of test
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(testLauncherDTO, result.getContent().get(0));

            verify(launcherRepository, times(1))
                    .findAll(any(Specification.class), any(Pageable.class));

            verify(dtoConverter, times(result.getSize()))
                    .convertToDto(testLauncher, LauncherDTO.class);
        }
    }
}