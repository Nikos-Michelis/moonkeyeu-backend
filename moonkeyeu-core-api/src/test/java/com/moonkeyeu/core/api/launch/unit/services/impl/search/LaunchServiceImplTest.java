package com.moonkeyeu.core.api.launch.unit.services.impl.search;

import com.moonkeyeu.core.api.launch.services.impl.search.LaunchServiceImpl;
import com.moonkeyeu.core.api.utils.mapper.DtoConverter;
import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.launch.LaunchDTO;
import com.moonkeyeu.core.api.launch.dto.launch.LaunchNormalDTO;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.model.launch.Launch;
import com.moonkeyeu.core.api.launch.repository.LaunchRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("LaunchServiceImpl Unit Tests")
class LaunchServiceImplTest {
    @Mock
    private LaunchRepository launchRepository;
    @Mock
    private DtoConverter dtoConverter;
    @InjectMocks
    private LaunchServiceImpl launchService;
    private PageSortingDTO testPageSortingDTO;
    private Map<String, String> testRequestParams;
    private Launch testLaunch;
    private LaunchNormalDTO testLaunchNormalDTO;
    private LaunchDTO testLaunchDTO;


    @BeforeEach
    void setUp() {
        this.testPageSortingDTO = PageSortingDTO.builder()
                .page(0)
                .limit(12)
                .field("net")
                .sort("asc")
                .build();

        this.testRequestParams = new HashMap<>();
        this.testRequestParams.put("upcoming", "true");
        this.testRequestParams.put("agency", "121");

        this.testLaunch = new Launch();
        this.testLaunch.setLaunchId("test_267f232e-f34a-4c79-ac56-b00cf69dd32d");
        this.testLaunch.setLaunchName("Test Falcon 9");
        Instant now = Instant.now();
        this.testLaunch.setNet(now);
        this.testLaunch.setWindowStart(now.minus(1, ChronoUnit.DAYS));
        this.testLaunch.setWindowEnd(now.plus(1, ChronoUnit.DAYS));

        this.testLaunchNormalDTO = new LaunchNormalDTO();
        this.testLaunchNormalDTO.setLaunchId(testLaunch.getLaunchId());
        this.testLaunchNormalDTO.setLaunchName(testLaunch.getLaunchName());
        this.testLaunchNormalDTO.setNet(testLaunch.getNet());
        this.testLaunchNormalDTO.setWindowStart(testLaunch.getWindowStart());
        this.testLaunchNormalDTO.setWindowEnd(testLaunch.getWindowEnd());

        this.testLaunchDTO = new LaunchDTO();
        this.testLaunchDTO.setLaunchId(testLaunch.getLaunchId());
        this.testLaunchDTO.setLaunchName(testLaunch.getLaunchName());
        this.testLaunchDTO.setNet(testLaunch.getNet());
        this.testLaunchDTO.setWindowStart(testLaunch.getWindowStart());
        this.testLaunchDTO.setWindowEnd(testLaunch.getWindowEnd());
    }

    @Nested
    @DisplayName("Search Launch by Filters")
    class searchLaunchByParamsTests {

        @Test
        @DisplayName("Should Return Paged Launch Normal Summaries When No Filters Are Applied")
        void shouldReturnPagedLaunchesWithoutFilters() {

            // given: service method going to test
            Page<Launch> launchPage = new PageImpl<>(List.of(testLaunch));

            when(LaunchServiceImplTest.this.launchRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(launchPage);

            when(LaunchServiceImplTest.this.dtoConverter.convertToDto(eq(testLaunch), eq(LaunchNormalDTO.class)))
                    .thenReturn(testLaunchNormalDTO);

            // when: when test runs
            Page<DTOEntity> result =
                    launchService.searchLaunch(Collections.emptyMap(), testPageSortingDTO);

            // then: result of test
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertSame(testLaunchNormalDTO, result.getContent().get(0));

            verify(LaunchServiceImplTest.this.launchRepository, times(1))
                    .findAll(any(Specification.class), any(Pageable.class));

            verify(LaunchServiceImplTest.this.dtoConverter, times(result.getSize()))
                    .convertToDto(testLaunch, LaunchNormalDTO.class);
        }

        @Test
        @DisplayName("Should Return Paged Launch Normal Summaries When Filters Are Applied")
        void shouldReturnPagedLaunchesWithFilters() {

            // given: service method going to test
            Page<Launch> launchPage = new PageImpl<>(List.of(testLaunch));

            when(LaunchServiceImplTest.this.launchRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(launchPage);

            when(LaunchServiceImplTest.this.dtoConverter.convertToDto(eq(testLaunch), eq(LaunchNormalDTO.class)))
                    .thenReturn(testLaunchNormalDTO);

            // when: when test runs
            Page<DTOEntity> result =
                    launchService.searchLaunch(LaunchServiceImplTest.this.testRequestParams, testPageSortingDTO);

            // then: result of test
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertSame(testLaunchNormalDTO, result.getContent().get(0));

            verify(LaunchServiceImplTest.this.launchRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            verify(dtoConverter, times(result.getSize())).convertToDto(testLaunch, LaunchNormalDTO.class);
        }
    }

    @Nested
    @DisplayName("Find Launch By ID")
    class findLaunchById {
        @Test
        @DisplayName("Should Return a Launch When ID Exists")
        void shouldReturnLaunchById() {

            // given
            final String launchId = "test_267f232e-f34a-4c79-ac56-b00cf69dd32d";

            when(LaunchServiceImplTest.this.launchRepository.findLaunchWithLaunchId(launchId))
                    .thenReturn(Optional.of(testLaunch));

            when(LaunchServiceImplTest.this.dtoConverter.convertToDto(testLaunch, LaunchDTO.class))
                    .thenReturn(testLaunchDTO);

            // when
            DTOEntity result = launchService.getLaunchById(launchId);

            // then
            assertNotNull(result);
            assertEquals(testLaunchDTO, result);

            verify(LaunchServiceImplTest.this.launchRepository).findLaunchWithLaunchId(launchId);
            verify(LaunchServiceImplTest.this.dtoConverter).convertToDto(testLaunch, LaunchDTO.class);
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Launch ID Is Null")
        void shouldHandleNullLaunchId() {

            // given
            final String launchId = null;

            when(LaunchServiceImplTest.this.launchRepository.findLaunchWithLaunchId(launchId))
                    .thenReturn(Optional.empty());

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> LaunchServiceImplTest.this.launchService.getLaunchById(launchId));

            assertNotNull(exception);
            assertEquals("Launch not found with id: " + launchId, exception.getMessage());
            verify(LaunchServiceImplTest.this.launchRepository, times(1)).findLaunchWithLaunchId(launchId);
            verifyNoInteractions(LaunchServiceImplTest.this.dtoConverter);
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Launch Configuration ID Not Found")
        void shouldThrowResourceNotFoundException() {

            // given
            final String launchId = "test_267f232e-f34a-4c79-ac56-b00cf69dd32d";

            when(LaunchServiceImplTest.this.launchRepository.findLaunchWithLaunchId(launchId))
                    .thenReturn(Optional.empty());

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> LaunchServiceImplTest.this.launchService.getLaunchById(launchId));

            assertNotNull(exception);
            assertEquals("Launch not found with id: " + launchId, exception.getMessage());
            verify(LaunchServiceImplTest.this.launchRepository).findLaunchWithLaunchId(launchId);
            verifyNoInteractions(LaunchServiceImplTest.this.dtoConverter);
        }
    }
}