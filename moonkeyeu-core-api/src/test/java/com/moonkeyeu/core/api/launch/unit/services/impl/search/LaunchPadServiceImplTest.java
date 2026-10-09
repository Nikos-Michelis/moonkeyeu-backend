package com.moonkeyeu.core.api.launch.unit.services.impl.search;

import com.moonkeyeu.core.api.launch.services.impl.search.LaunchPadServiceImpl;
import com.moonkeyeu.core.api.utils.mapper.DtoConverter;
import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.launch.LaunchNormalDTO;
import com.moonkeyeu.core.api.launch.dto.pad.LaunchPadDTO;
import com.moonkeyeu.core.api.launch.dto.pad.LaunchPadDetailedDTO;
import com.moonkeyeu.core.api.launch.model.launch.Launch;
import com.moonkeyeu.core.api.launch.model.pad.LaunchPad;
import com.moonkeyeu.core.api.launch.repository.LaunchPadRepository;
import com.moonkeyeu.core.api.launch.repository.LaunchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LaunchPadServiceImplTest Unit Tests")
class LaunchPadServiceImplTest {
    @Mock
    private LaunchPadRepository launchPadRepository;
    @Mock
    private LaunchRepository launchRepository;
    @Mock
    private DtoConverter dtoConverter;
    @InjectMocks
    private LaunchPadServiceImpl launchPadService;
    private List<LaunchPad> launchPadRepositoryData;
    private LaunchPad testLaunchPad;
    private Launch testLaunch;
    private LaunchPadDetailedDTO testLaunchPadDetailedDTO;


    @BeforeEach
    void setUp() {
        LaunchPad launchPad1 = new LaunchPad();
        launchPad1.setLaunchPadId(1L);
        launchPad1.setLaunchPadName("test launch pad 1");
        launchPad1.setActive(true);

        LaunchPad launchPad2 = new LaunchPad();
        launchPad2.setLaunchPadId(2L);
        launchPad2.setLaunchPadName("test launch pad 2");
        launchPad2.setActive(false);

        LaunchPad launchPad3 = new LaunchPad();
        launchPad3.setLaunchPadId(3L);
        launchPad3.setLaunchPadName("test launch pad 3");
        launchPad3.setActive(false);

        LaunchPad launchPad4 = new LaunchPad();
        launchPad4.setLaunchPadId(4L);
        launchPad4.setLaunchPadName("test launch pad 4");
        launchPad4.setActive(false);

        this.launchPadRepositoryData = List.of(launchPad1, launchPad2, launchPad3, launchPad4);

        this.testLaunch = new Launch();
        this.testLaunch.setLaunchId("test_bf08a10b-35f0-4736-97f3-ba111e59cd55");
        this.testLaunch.setLaunchName("Space Shuttle");
        Instant now = Instant.now();
        this.testLaunch.setNet(now);
        this.testLaunch.setWindowStart(now.minus(1, ChronoUnit.DAYS));
        this.testLaunch.setWindowEnd(now.plus(1, ChronoUnit.DAYS));

        LaunchNormalDTO testLaunchNormalDTO = new LaunchNormalDTO();
        testLaunchNormalDTO.setLaunchId(testLaunch.getLaunchId());
        testLaunchNormalDTO.setLaunchName(testLaunch.getLaunchName());
        testLaunchNormalDTO.setNet(testLaunch.getNet());

        this.testLaunchPad = new LaunchPad();
        this.testLaunchPad.setLaunchPadId(2L);
        this.testLaunchPad.setLaunchPadName("test launch pad 2");

        this.testLaunchPadDetailedDTO = new LaunchPadDetailedDTO();
        this.testLaunchPadDetailedDTO.setLaunchPadId(testLaunchPad.getLaunchPadId());
        this.testLaunchPadDetailedDTO.setLaunchPadName(testLaunchPad.getLaunchPadName());
        this.testLaunchPadDetailedDTO.setUpcomingLaunches(testLaunchNormalDTO);
    }

    @Test
    @DisplayName("Should Return All LaunchPads Grouped By Active Status")
    void shouldReturnAllLaunchPads() {

        when(launchPadRepository.findAll())
                .thenReturn(this.launchPadRepositoryData);

        when(dtoConverter.convertToDto(any(), eq(LaunchPadDTO.class)))
                .thenAnswer(invocation -> {
                    LaunchPad source = invocation.getArgument(0);
                    LaunchPadDTO dto = new LaunchPadDTO();
                    dto.setLaunchPadId(source.getLaunchPadId());
                    dto.setName(source.getLaunchPadName());
                    dto.setActive(source.isActive());
                    return dto;
                });

        Map<String, Object> result = launchPadService.getAllLaunchPads();

        int activeCount = (int) result.get("active");
        int inactiveCount = (int) result.get("inactive");
        List<LaunchPadDTO> pads = (List<LaunchPadDTO>) result.get("pads");

        assertEquals(1, activeCount);
        assertEquals(3, inactiveCount);
        assertEquals(4, pads.size());

        verify(launchPadRepository, times(1)).findAll();
        verify(dtoConverter, times(4))
                .convertToDto(any(), eq(LaunchPadDTO.class));
    }

    @Nested
    @DisplayName("Find LaunchPad By ID")
    class FindLaunchPadById {
        @Test
        @DisplayName("Should Return a LaunchPad When ID Exists")
        void shouldReturnLaunchPadById() {
            // given
            Integer launchPadId = 2;

            when(launchPadRepository.findLaunchPadWithPadId(launchPadId))
                    .thenReturn(Optional.of(testLaunchPad));

            when(launchRepository.findUpcomingLaunchesByLaunchPadId(launchPadId))
                    .thenReturn(Optional.of(testLaunch));

            when(dtoConverter.convertToDto(testLaunchPad, LaunchPadDetailedDTO.class))
                    .thenReturn(testLaunchPadDetailedDTO);

            // when
            DTOEntity result = launchPadService.getLaunchPadById(launchPadId);

            // then
            assertNotNull(result);
            assertEquals(testLaunchPadDetailedDTO, result);

            verify(launchPadRepository).findLaunchPadWithPadId(launchPadId);
            verify(launchRepository).findUpcomingLaunchesByLaunchPadId(launchPadId);
            verify(dtoConverter).convertToDto(testLaunchPad, LaunchPadDetailedDTO.class);
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When LaunchPad ID Is Null")
        void shouldHandleNullLaunchPadId() {
            // given
            Integer launchPadId = null;

            when(launchPadRepository.findLaunchPadWithPadId(launchPadId))
                    .thenReturn(Optional.empty());

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> launchPadService.getLaunchPadById(launchPadId));

            assertNotNull(exception);
            assertEquals("LaunchPad not found with id: " + launchPadId, exception.getMessage());
            verify(launchPadRepository, times(1)).findLaunchPadWithPadId(launchPadId);
            verifyNoInteractions(launchRepository);
            verifyNoInteractions(dtoConverter);
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When LaunchPad ID Not Found")
        void shouldThrowResourceNotFoundException() {
            // given
            Integer launchPadId = 132;

            when(launchPadRepository.findLaunchPadWithPadId(launchPadId))
                    .thenReturn(Optional.empty());

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> launchPadService.getLaunchPadById(launchPadId));

            assertNotNull(exception);
            assertEquals("LaunchPad not found with id: " + launchPadId, exception.getMessage());
            verify(launchPadRepository, times(1)).findLaunchPadWithPadId(launchPadId);
            verifyNoInteractions(launchRepository);
            verifyNoInteractions(dtoConverter);
        }
    }
}