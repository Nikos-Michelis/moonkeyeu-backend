package com.moonkeyeu.core.api.launch.unit.services.impl.search;

import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.dto.spacecraft.SpacecraftConfigSummarizedDTO;
import com.moonkeyeu.core.api.launch.dto.spacecraft.SpacecraftConfigurationDTO;
import com.moonkeyeu.core.api.launch.model.spacecraft.SpacecraftConfiguration;
import com.moonkeyeu.core.api.launch.repository.SpacecraftRepository;
import com.moonkeyeu.core.api.launch.services.impl.search.SpacecraftServiceImpl;
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
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpacecraftServiceImplTest Unit Tests")
class SpacecraftServiceImplTest {
    @Mock
    private SpacecraftRepository spacecraftRepository;
    @Mock
    private DtoConverter dtoConverter;
    @InjectMocks
    private SpacecraftServiceImpl spacecraftService;
    private PageSortingDTO testPageSortingDTO;
    private Map<String, String> testRequestParams;
    private SpacecraftConfiguration testSpacecraftConfiguration;
    private SpacecraftConfigurationDTO testSpacecraftConfigurationDTO;
    private SpacecraftConfigSummarizedDTO testSpacecraftConfigSummarizedDTO;


    @BeforeEach
    void setUp() {
        this.testPageSortingDTO = PageSortingDTO.builder()
                .page(0)
                .limit(12)
                .field("net")
                .sort("asc")
                .build();

        this.testRequestParams = new HashMap<>();
        this.testRequestParams.put("search", "Crew Dragon 2");

        this.testSpacecraftConfiguration = new SpacecraftConfiguration();
        this.testSpacecraftConfiguration.setSpacecraftConfId(12L);
        this.testSpacecraftConfiguration.setSpacecraftConfName("Crew Dragon 2");
        this.testSpacecraftConfiguration.setInUse(true);


        this.testSpacecraftConfigSummarizedDTO = new SpacecraftConfigSummarizedDTO();
        this.testSpacecraftConfigSummarizedDTO.setSpacecraftConfId(testSpacecraftConfiguration.getSpacecraftConfId());
        this.testSpacecraftConfigSummarizedDTO.setSpacecraftConfName(testSpacecraftConfiguration.getSpacecraftConfName());

        this.testSpacecraftConfigurationDTO = new SpacecraftConfigurationDTO();
        this.testSpacecraftConfigSummarizedDTO.setSpacecraftConfId(testSpacecraftConfiguration.getSpacecraftConfId());
        this.testSpacecraftConfigSummarizedDTO.setSpacecraftConfName(testSpacecraftConfiguration.getSpacecraftConfName());
    }

    @Nested
    @DisplayName("Search Spacecraft Configurations By Filters")
    class SearchSpacecraftConfigsTests {

        @Test
        @DisplayName("Should Return Paged Spacecraft Configuration Summaries When No Filters Are Applied")
        void shouldReturnPagedSpacecraftConfigsWithoutFilters() {
            // given: service method going to test
            Page<SpacecraftConfiguration> spacecraftConfigsPage = new PageImpl<>(List.of(testSpacecraftConfiguration));

            when(spacecraftRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(spacecraftConfigsPage);

            when(dtoConverter.convertToDto(eq(testSpacecraftConfiguration), eq(SpacecraftConfigSummarizedDTO.class)))
                    .thenReturn(testSpacecraftConfigSummarizedDTO);

            // when: when test runs
            Page<DTOEntity> result =
                    spacecraftService.searchSpacecraft(Collections.emptyMap(), testPageSortingDTO);

            // then: result of test
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertSame(testSpacecraftConfigSummarizedDTO, result.getContent().get(0));

            verify(spacecraftRepository, times(1))
                    .findAll(any(Specification.class), any(Pageable.class));

            verify(dtoConverter, times(result.getSize()))
                    .convertToDto(testSpacecraftConfiguration, SpacecraftConfigSummarizedDTO.class);
        }

        @Test
        @DisplayName("Should Return Paged Spacecraft Configuration Summaries When Filters Are Applied")
        void shouldReturnPagedSpacecraftConfigsWithFilters() {
            // given: service method going to test
            Page<SpacecraftConfiguration> spacecraftConfigsPage = new PageImpl<>(List.of(testSpacecraftConfiguration));

            when(spacecraftRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(spacecraftConfigsPage);

            when(dtoConverter.convertToDto(eq(testSpacecraftConfiguration), eq(SpacecraftConfigSummarizedDTO.class)))
                    .thenReturn(testSpacecraftConfigSummarizedDTO);

            // when: when test runs
            Page<DTOEntity> result =
                    spacecraftService.searchSpacecraft(testRequestParams, testPageSortingDTO);

            // then: result of test
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertSame(testSpacecraftConfigSummarizedDTO, result.getContent().get(0));

            verify(spacecraftRepository, times(1))
                    .findAll(any(Specification.class), any(Pageable.class));

            verify(dtoConverter, times(result.getSize()))
                    .convertToDto(testSpacecraftConfiguration, SpacecraftConfigSummarizedDTO.class);
        }
    }

    @Nested
    @DisplayName("Find SpacecraftConfig By ID")
    class FindSpacecraftConfigById {
        @Test
        @DisplayName("Should Return a Spacecraft Configuration When ID Exists")
        void shouldReturnSpacecraftConfigById() {
            // given
            final Integer spacecraftConfigId = 12;

            when(spacecraftRepository.findSpacecraftWithSpacecraftId(spacecraftConfigId))
                    .thenReturn(Optional.of(testSpacecraftConfiguration));

            when(dtoConverter.convertToDto(testSpacecraftConfiguration, SpacecraftConfigurationDTO.class))
                    .thenReturn(testSpacecraftConfigurationDTO);

            // when
            DTOEntity result = spacecraftService.getSpacecraftById(spacecraftConfigId);

            // then
            assertNotNull(result);
            assertEquals(testSpacecraftConfigurationDTO, result);

            verify(spacecraftRepository).findSpacecraftWithSpacecraftId(spacecraftConfigId);
            verify(dtoConverter).convertToDto(testSpacecraftConfiguration, SpacecraftConfigurationDTO.class);
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Spacecraft Configuration ID Is Null")
        void shouldHandleNullSpacecraftConfigId() {
            // given
            final Integer spacecraftConfigId = 12;

            when(spacecraftRepository.findSpacecraftWithSpacecraftId(spacecraftConfigId))
                    .thenReturn(Optional.empty());

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> spacecraftService.getSpacecraftById(spacecraftConfigId));

            assertNotNull(exception);
            assertEquals("Spacecraft configuration not found with id: " + spacecraftConfigId, exception.getMessage());
            verify(spacecraftRepository, times(1)).findSpacecraftWithSpacecraftId(spacecraftConfigId);
            verifyNoInteractions(dtoConverter);
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Spacecraft Configuration ID Not Found")
        void shouldThrowResourceNotFoundException() {
            // given
            final Integer spacecraftConfigId = 12;

            when(spacecraftRepository.findSpacecraftWithSpacecraftId(spacecraftConfigId))
                    .thenReturn(Optional.empty());

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> spacecraftService.getSpacecraftById(spacecraftConfigId));

            assertNotNull(exception);
            assertEquals("Spacecraft configuration not found with id: " + spacecraftConfigId, exception.getMessage());
            verify(spacecraftRepository, times(1)).findSpacecraftWithSpacecraftId(spacecraftConfigId);
            verifyNoInteractions(dtoConverter);
        }
    }
}