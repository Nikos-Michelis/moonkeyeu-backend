package com.moonkeyeu.core.api.launch.unit.services.impl.search;

import com.moonkeyeu.core.api.launch.services.impl.search.RocketServiceImpl;
import com.moonkeyeu.core.api.utils.mapper.DtoConverter;
import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.dto.rocket.RocketConfigSummarizedDTO;
import com.moonkeyeu.core.api.launch.dto.rocket.RocketConfigurationDTO;
import com.moonkeyeu.core.api.launch.dto.rocket.RocketNormalDTO;
import com.moonkeyeu.core.api.launch.model.rocket.Rocket;
import com.moonkeyeu.core.api.launch.model.rocket.RocketConfiguration;
import com.moonkeyeu.core.api.launch.repository.RocketsRepository;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RocketServiceImplTest Unit Tests")
class RocketServiceImplTest {
    @Mock
    private RocketsRepository rocketsRepository;
    @Mock
    private DtoConverter dtoConverter;
    @InjectMocks
    private RocketServiceImpl rocketService;
    private PageSortingDTO testPageSortingDTO;
    private Map<String, String> testRequestParams;
    private Rocket testRocket;
    private RocketConfiguration testRocketConfiguration;
    private RocketNormalDTO testRocketNormalDTO;
    private RocketConfigSummarizedDTO testRocketConfigSummarizedDTO;


    @BeforeEach
    void setUp() {
        this.testPageSortingDTO = PageSortingDTO.builder()
                .page(1)
                .limit(12)
                .field("net")
                .sort("desc")
                .build();

        this.testRequestParams = new HashMap<>();
        this.testRequestParams.put("reusable", "true");
        this.testRequestParams.put("agency", "121");
        this.testRequestParams.put("active", "true");
        this.testRequestParams.put("search", "true");

        this.testRocketConfiguration = new RocketConfiguration();
        this.testRocketConfiguration.setRocketConfId(2L);
        this.testRocketConfiguration.setRocketName("Falcon 9 Block 5 | Crew-12");
        this.testRocketConfiguration.setActive(true);

        this.testRocket = new Rocket();
        this.testRocket.setRocketId(1L);
        this.testRocket.setRocketConfiguration(this.testRocketConfiguration);

        this.testRocketConfigSummarizedDTO = new RocketConfigSummarizedDTO();
        this.testRocketConfigSummarizedDTO.setRocketConfId(testRocketConfiguration.getRocketConfId());
        this.testRocketConfigSummarizedDTO.setFullname(testRocketConfiguration.getRocketName());
        this.testRocketConfigSummarizedDTO.setActive(testRocketConfiguration.getActive());

        RocketConfigurationDTO testRocketConfigurationDTO = new RocketConfigurationDTO();
        testRocketConfigurationDTO.setRocketConfId(testRocketConfiguration.getRocketConfId());
        testRocketConfigurationDTO.setRocketName(testRocketConfiguration.getRocketName());
        testRocketConfigurationDTO.setActive(testRocketConfiguration.getActive());

        this.testRocketNormalDTO = new RocketNormalDTO();
        this.testRocketNormalDTO.setRocketId(testRocket.getRocketId());
        this.testRocketNormalDTO.setRocketConfiguration(testRocketConfigurationDTO);

    }

    @Nested
    @DisplayName("Search Rocket, Rocket Configuration by Filters")
    class SearchRocketTests {

        @Test
        @DisplayName("Should Return Paged Rocket Configuration Summaries When No Filters Are Applied")
        void shouldReturnPagedRocketConfigsWithoutFilters() {
            // given: service method going to test
            Page<RocketConfiguration> rocketPage = new PageImpl<>(List.of(testRocketConfiguration));

            when(rocketsRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(rocketPage);

            when(dtoConverter.convertToDto(eq(testRocketConfiguration), eq(RocketConfigSummarizedDTO.class)))
                    .thenReturn(testRocketConfigSummarizedDTO);

            // when: when test runs
            Page<DTOEntity> result =
                    rocketService.searchRocket(null, testPageSortingDTO);

            // then: result of test
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertSame(testRocketConfigSummarizedDTO, result.getContent().get(0));

            verify(rocketsRepository, times(1))
                    .findAll(any(Specification.class), any(Pageable.class));

            verify(dtoConverter, times(result.getSize()))
                    .convertToDto(testRocketConfiguration, RocketConfigSummarizedDTO.class);
        }

        @Test
        @DisplayName("Should Return Paged Rocket Configuration Summaries When Filters Are Applied")
        void shouldReturnPagedRocketsWithFilters() {
            // given: service method going to test
            Page<RocketConfiguration> rocketPage = new PageImpl<>(List.of(testRocketConfiguration));

            when(rocketsRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(rocketPage);

            when(dtoConverter.convertToDto(eq(testRocketConfiguration), eq(RocketConfigSummarizedDTO.class)))
                    .thenReturn(testRocketConfigSummarizedDTO);

            // when: when test runs
            Page<DTOEntity> result =
                    rocketService.searchRocket(testRequestParams, testPageSortingDTO);

            // then: result of test
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertSame(testRocketConfigSummarizedDTO, result.getContent().get(0));

            verify(rocketsRepository, times(1))
                    .findAll(any(Specification.class), any(Pageable.class));

            verify(dtoConverter, times(result.getSize()))
                    .convertToDto(testRocketConfiguration, RocketConfigSummarizedDTO.class);
        }
    }
    @Nested
    @DisplayName("Find Rocket By ID")
    class FindRocketById {
        @Test
        @DisplayName("Should return a Rocket When ID Exists")
        void shouldReturnRocketById() {
            // given
            final Integer rocketId = 12;

            when(rocketsRepository.findRocketWithRocketId(rocketId))
                    .thenReturn(Optional.of(testRocket));

            when(dtoConverter.convertToDto(testRocket, RocketNormalDTO.class))
                    .thenReturn(testRocketNormalDTO);

            // when
            DTOEntity result = rocketService.getRocketById(rocketId);

            // then
            assertNotNull(result);
            assertEquals(testRocketNormalDTO, result);

            verify(rocketsRepository).findRocketWithRocketId(rocketId);
            verify(dtoConverter).convertToDto(testRocket, RocketNormalDTO.class);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException When Rocket ID Is Null")
        void shouldHandleNullRocketId() {
            // given
            final Integer rocketId = null;

            when(rocketsRepository.findRocketWithRocketId(rocketId))
                    .thenReturn(Optional.empty());

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> rocketService.getRocketById(rocketId));

            assertNotNull(exception);
            assertEquals("Rocket not found with id: " + rocketId, exception.getMessage());
            verify(rocketsRepository, times(1)).findRocketWithRocketId(rocketId);
            verifyNoInteractions(dtoConverter);
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Rocket ID Not Found")
        void shouldThrowResourceNotFoundException() {
            // given
            final Integer rocketId = 123;

            when(rocketsRepository.findRocketWithRocketId(rocketId))
                    .thenReturn(Optional.empty());

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> rocketService.getRocketById(rocketId));

            assertNotNull(exception);
            assertEquals("Rocket not found with id: " + rocketId, exception.getMessage());
            verify(rocketsRepository, times(1)).findRocketWithRocketId(rocketId);
            verifyNoInteractions(dtoConverter);
        }
    }
}