package com.moonkeyeu.core.api.launch.services.impl.search;

import com.moonkeyeu.core.api.utils.mapper.DtoConverter;
import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.agency.AgencyDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.agency.AgencySummarizedDTO;
import com.moonkeyeu.core.api.launch.model.agency.Agencies;
import com.moonkeyeu.core.api.launch.model.launch.Launch;
import com.moonkeyeu.core.api.launch.repository.AgenciesRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("AgenciesServiceImplTest Unit Tests")
class AgenciesServiceImplTest {

    @Mock
    private AgenciesRepository agenciesRepository;
    @Mock
    private LaunchRepository launchRepository;
    @Mock
    private DtoConverter dtoConverter;
    @InjectMocks
    private AgenciesServiceImpl agenciesService;
    private Agencies testAgencies;
    private Launch testLaunch;
    private AgencySummarizedDTO testAgencySummarizedDTO;
    private AgencyDetailedDTO testAgencyDetailedDTO;

    @BeforeEach
    void setUp() {
        this.testLaunch = new Launch();
        this.testLaunch.setLaunchId("test_267f232e-f34a-4c79-ac56-b00cf69dd32d");
        this.testLaunch.setLaunchName("Test Falcon 9");
        Instant now = Instant.now();
        this.testLaunch.setNet(now);
        this.testLaunch.setWindowStart(now.minus(1, ChronoUnit.DAYS));
        this.testLaunch.setWindowEnd(now.plus(1, ChronoUnit.DAYS));

        this.testAgencies = new Agencies();
        this.testAgencies.setAgencyId(1L);
        this.testAgencies.setAgencyName("SpaceX");

        this.testAgencySummarizedDTO = new AgencySummarizedDTO();
        this.testAgencySummarizedDTO.setAgencyId(testAgencies.getAgencyId());
        this.testAgencySummarizedDTO.setAgencyName(testAgencies.getAgencyName());

        this.testAgencyDetailedDTO = new AgencyDetailedDTO();
        this.testAgencyDetailedDTO.setAgencyId(testAgencies.getAgencyId());
        this.testAgencyDetailedDTO.setAgencyName(testAgencies.getAgencyName());

    }

    @Test
    @DisplayName("Should return all featured agencies")
    void shouldReturnAllFeaturedAgencies() {

        when(agenciesRepository.findAll())
                .thenReturn(List.of(this.testAgencies));

        when(dtoConverter.convertToDto(eq(testAgencies), eq(AgencySummarizedDTO.class)))
                .thenReturn(this.testAgencySummarizedDTO);

        List<DTOEntity> result = agenciesService.getAllAgencies();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAgencySummarizedDTO, result.get(0));

        verify(agenciesRepository, times(1))
                .findAll();
        verify(dtoConverter, times(1))
                .convertToDto(any(), eq(AgencySummarizedDTO.class));
    }

    @Nested
    @DisplayName("find agency by id tests")
    class FindAgencyByIdTests {
        @Test
        @DisplayName("Should return agency by id")
        void shouldReturnAgencyById() {
            // given
            Integer agencyId = 1;

            when(agenciesRepository.findAgenciesById(agencyId))
                    .thenReturn(Optional.of(testAgencies));

            when(launchRepository.findUpcomingLaunchesByAgencyId(agencyId))
                    .thenReturn(Optional.of(testLaunch));

            when(dtoConverter.convertToDto(testAgencies, AgencyDetailedDTO.class))
                    .thenReturn(testAgencyDetailedDTO);

            // when
            DTOEntity result = agenciesService.getAgencyById(agencyId);

            // then
            assertNotNull(result);
            assertEquals(testAgencyDetailedDTO, result);

            verify(agenciesRepository).findAgenciesById(agencyId);
            verify(launchRepository).findUpcomingLaunchesByAgencyId(agencyId);
            verify(dtoConverter).convertToDto(testAgencies, AgencyDetailedDTO.class);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when agencyId is null")
        void shouldHandleNullAgencyId() {
            // given
            Integer agencyId = null;

            when(agenciesRepository.findAgenciesById(agencyId))
                    .thenReturn(Optional.empty());

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> agenciesService.getAgencyById(agencyId));

            assertNotNull(exception);
            assertEquals("Agency not found with id: " + agencyId, exception.getMessage());
            verify(agenciesRepository, times(1)).findAgenciesById(agencyId);
            verifyNoInteractions(launchRepository);
            verifyNoInteractions(dtoConverter);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when agency not found")
        void shouldThrowResourceNotFoundException() {
            // given
            Integer agencyId = 123;

            when(agenciesRepository.findAgenciesById(agencyId))
                    .thenReturn(Optional.empty());

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> agenciesService.getAgencyById(agencyId));

            assertNotNull(exception);
            assertEquals("Agency not found with id: " + agencyId, exception.getMessage());
            verify(agenciesRepository, times(1)).findAgenciesById(agencyId);
            verifyNoInteractions(launchRepository);
            verifyNoInteractions(dtoConverter);
        }
    }
}