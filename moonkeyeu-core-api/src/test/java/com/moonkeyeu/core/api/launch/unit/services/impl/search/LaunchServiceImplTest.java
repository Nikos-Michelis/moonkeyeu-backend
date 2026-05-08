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
                .page(1)
                .limit(12)
                .field("net")
                .sort("asc")
                .build();

        this.testRequestParams = new HashMap<>();
        this.testRequestParams.put("upcoming", "false");
        this.testRequestParams.put("agency", "121");
        this.testRequestParams.put("astronaut", "712");
        this.testRequestParams.put("location", "12");
        this.testRequestParams.put("pad", "80");
        this.testRequestParams.put("launcher", "240");
        this.testRequestParams.put("rocketConfig", "87");
        this.testRequestParams.put("spacecraftConfig", "6");
        this.testRequestParams.put("program", "17");
        this.testRequestParams.put("search", "Falcon 9 Block 5 | Crew-12");

        this.testLaunch = new Launch();
        this.testLaunch.setLaunchId("862b54e3-7bf8-48e5-8cd1-d77257d5fde9");
        this.testLaunch.setLaunchName("Falcon 9 Block 5 | Crew-12");
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

            when(launchRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(launchPage);

            when(dtoConverter.convertToDto(eq(testLaunch), eq(LaunchNormalDTO.class)))
                    .thenReturn(testLaunchNormalDTO);

            // when: when test runs
            Page<DTOEntity> result =
                    launchService.searchLaunch(null, testPageSortingDTO);

            // then: result of test
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertSame(testLaunchNormalDTO, result.getContent().getFirst());

            verify(launchRepository, times(1))
                    .findAll(any(Specification.class), any(Pageable.class));

            verify(dtoConverter, times(result.getSize()))
                    .convertToDto(testLaunch, LaunchNormalDTO.class);
        }

        @Test
        @DisplayName("Should Return Paged Launch Normal Summaries When Filters Are Applied")
        void shouldReturnPagedLaunchesWithFilters() {

            // given: service method going to test
            Page<Launch> launchPage = new PageImpl<>(List.of(testLaunch));

            when(launchRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(launchPage);

            when(dtoConverter.convertToDto(eq(testLaunch), eq(LaunchNormalDTO.class)))
                    .thenReturn(testLaunchNormalDTO);

            // when: when test runs
            Page<DTOEntity> result =
                    launchService.searchLaunch(testRequestParams, testPageSortingDTO);

            // then: result of test
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertSame(testLaunchNormalDTO, result.getContent().get(0));

            verify(launchRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
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
            final String launchId = "test_bf08a10b-35f0-4736-97f3-ba111e59cd55";

            when(launchRepository.findLaunchWithLaunchId(launchId))
                    .thenReturn(Optional.of(testLaunch));

            when(dtoConverter.convertToDto(testLaunch, LaunchDTO.class))
                    .thenReturn(testLaunchDTO);

            // when
            DTOEntity result = launchService.getLaunchById(launchId);

            // then
            assertNotNull(result);
            assertEquals(testLaunchDTO, result);

            verify(launchRepository).findLaunchWithLaunchId(launchId);
            verify(dtoConverter).convertToDto(testLaunch, LaunchDTO.class);
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Launch ID Is Null")
        void shouldHandleNullLaunchId() {

            // given
            final String launchId = null;

            when(launchRepository.findLaunchWithLaunchId(launchId))
                    .thenReturn(Optional.empty());

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> launchService.getLaunchById(launchId));

            assertNotNull(exception);
            assertEquals("Launch not found with id: " + launchId, exception.getMessage());
            verify(launchRepository, times(1)).findLaunchWithLaunchId(launchId);
            verifyNoInteractions(dtoConverter);
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Launch Configuration ID Not Found")
        void shouldThrowResourceNotFoundException() {

            // given
            final String launchId = "test_not_exists_f08a10b-35f0-4736-97f3-ba111e59cd55";

            when(launchRepository.findLaunchWithLaunchId(launchId))
                    .thenReturn(Optional.empty());

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> launchService.getLaunchById(launchId));

            assertNotNull(exception);
            assertEquals("Launch not found with id: " + launchId, exception.getMessage());
            verify(launchRepository).findLaunchWithLaunchId(launchId);
            verifyNoInteractions(dtoConverter);
        }
    }
}