package com.moonkeyeu.core.api.launch.unit.services.impl.search;

import com.moonkeyeu.core.api.launch.services.impl.search.ProgramsServiceImpl;
import com.moonkeyeu.core.api.utils.mapper.DtoConverter;
import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.dto.program.ProgramDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.program.ProgramSummarizedDTO;
import com.moonkeyeu.core.api.launch.model.launch.Launch;
import com.moonkeyeu.core.api.launch.model.program.Programs;
import com.moonkeyeu.core.api.launch.repository.LaunchRepository;
import com.moonkeyeu.core.api.launch.repository.ProgramsRepository;
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
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProgramsServiceImplTest Unit Tests")
class ProgramsServiceImplTest {
    @Mock
    private ProgramsRepository programsRepository;
    @Mock
    private LaunchRepository launchRepository;
    @Mock
    private DtoConverter dtoConverter;
    @InjectMocks
    private ProgramsServiceImpl programsService;
    private PageSortingDTO testPageSortingDTO;
    private Map<String, String> testRequestParams;
    private Programs testPrograms;
    private Launch testLaunch;
    private ProgramSummarizedDTO testProgramSummarizedDTO;
    private ProgramDetailedDTO testProgramDetailedDTO;

    @BeforeEach
    void setUp() {
        this.testPageSortingDTO = PageSortingDTO.builder()
                .page(1)
                .limit(12)
                .field("startDate")
                .sort("desc")
                .build();

        this.testRequestParams = new HashMap<>();
        this.testRequestParams.put("search", "Polaris");

        this.testPrograms = new Programs();
        this.testPrograms.setProgramId(40L);
        this.testPrograms.setName("Polaris");

        this.testLaunch = new Launch();
        this.testLaunch.setLaunchId("test_267f232e-f34a-4c79-ac56-b00cf69dd32d");
        this.testLaunch.setLaunchName("Test Falcon 9");
        Instant now = Instant.now();
        this.testLaunch.setNet(now);
        this.testLaunch.setWindowStart(now.minus(1, ChronoUnit.DAYS));
        this.testLaunch.setWindowEnd(now.plus(1, ChronoUnit.DAYS));

        this.testProgramSummarizedDTO = new ProgramSummarizedDTO();
        this.testProgramSummarizedDTO.setProgramId(testPrograms.getProgramId());
        this.testProgramSummarizedDTO.setName(testPrograms.getName());

        this.testProgramDetailedDTO = new ProgramDetailedDTO();
        this.testProgramDetailedDTO.setProgramId(testPrograms.getProgramId());
        this.testProgramDetailedDTO.setName(testPrograms.getName());
    }

    @Nested
    @DisplayName("Search Programs by Filters")
    class SerachAstronautTests {

        @Test
        @DisplayName("Should Return Paged Programs Summaries When No Filters Are Applied")
        void shouldReturnPagedProgramsWithoutFilters() {
            // given
            Page<Programs> programsPage = new PageImpl<>(List.of(testPrograms));

            when(programsRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(programsPage);

            when(dtoConverter.convertToDto(eq(testPrograms), eq(ProgramSummarizedDTO.class)))
                    .thenReturn(testProgramSummarizedDTO);

            // when
            Page<DTOEntity> result =
                    programsService.searchProgram(null, testPageSortingDTO);

            // then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(testProgramSummarizedDTO, result.getContent().get(0));

            verify(programsRepository, times(1))
                    .findAll(any(Specification.class), any(Pageable.class));

            verify(dtoConverter, times(result.getSize()))
                    .convertToDto(testPrograms, ProgramSummarizedDTO.class);
        }

        @Test
        @DisplayName("Should Return Paged Programs Summaries When Filters Are Applied")
        void shouldReturnPagedProgramsWithFilters() {
            // given
            Page<Programs> programsPage = new PageImpl<>(List.of(testPrograms));

            when(programsRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(programsPage);

            when(dtoConverter.convertToDto(eq(testPrograms), eq(ProgramSummarizedDTO.class)))
                    .thenReturn(testProgramSummarizedDTO);

            // when: when test runs
            Page<DTOEntity> result =
                    programsService.searchProgram(testRequestParams, testPageSortingDTO);

            // then: result of test
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(testProgramSummarizedDTO, result.getContent().get(0));

            verify(programsRepository, times(1))
                    .findAll(any(Specification.class), any(Pageable.class));

            verify(dtoConverter, times(result.getSize()))
                    .convertToDto(testPrograms, ProgramSummarizedDTO.class);
        }
    }

    @Nested
    @DisplayName("Find Program By ID")
    class findProgramById {
        @Test
        @DisplayName("Should Return a Program When ID Exists")
        void shouldReturnProgramById() {
            // given
            Integer programId = 40;

            when(programsRepository.findProgramById(programId))
                    .thenReturn(Optional.of(testPrograms));

            when(launchRepository.findUpcomingLaunchesByProgramId(programId))
                    .thenReturn(Optional.of(testLaunch));

            when(dtoConverter.convertToDto(testPrograms, ProgramDetailedDTO.class))
                    .thenReturn(testProgramDetailedDTO);

            // when
            DTOEntity result = programsService.getProgramById(programId);

            // then
            assertNotNull(result);
            assertEquals(testProgramDetailedDTO, result);

            verify(programsRepository).findProgramById(programId);
            verify(launchRepository).findUpcomingLaunchesByProgramId(programId);
            verify(dtoConverter).convertToDto(testPrograms, ProgramDetailedDTO.class);
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Program ID Is Null")
        void shouldHandleNullProgramId() {
            // given
            Integer programId = null;

            when(programsRepository.findProgramById(programId))
                    .thenReturn(Optional.empty());

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> programsService.getProgramById(programId));

            assertNotNull(exception);
            assertEquals("Program not found with id: " + programId, exception.getMessage());
            verify(programsRepository, times(1)).findProgramById(programId);
            verifyNoInteractions(launchRepository);
            verifyNoInteractions(dtoConverter);
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Program ID Not Found")
        void shouldThrowResourceNotFoundException() {
            // given
            final Integer programId = 123;

            when(programsRepository.findProgramById(programId))
                    .thenReturn(Optional.empty());

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> programsService.getProgramById(programId));

            assertNotNull(exception);
            assertEquals("Program not found with id: " + programId, exception.getMessage());
            verify(programsRepository, times(1)).findProgramById(programId);
            verifyNoInteractions(launchRepository);
            verifyNoInteractions(dtoConverter);
        }
    }
}