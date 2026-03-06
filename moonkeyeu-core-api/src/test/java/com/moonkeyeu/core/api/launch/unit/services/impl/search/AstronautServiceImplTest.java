package com.moonkeyeu.core.api.launch.unit.services.impl.search;

import com.moonkeyeu.core.api.launch.services.impl.search.AstronautServiceImpl;
import com.moonkeyeu.core.api.utils.mapper.DtoConverter;
import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.astronaut.AstronautDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.astronaut.AstronautNormalDTO;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.model.astronaut.Astronaut;
import com.moonkeyeu.core.api.launch.repository.AstronautsRepository;
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
@DisplayName("AstronautServiceImpl Unit Tests")
class AstronautServiceImplTest {
    @Mock
    private AstronautsRepository astronautsRepository;
    @Mock
    private DtoConverter dtoConverter;
    @InjectMocks
    private AstronautServiceImpl astronautService;
    private PageSortingDTO testPageSortingDTO;
    private Map<String, String> testRequestParams;
    private Astronaut testAstronaut;
    private AstronautNormalDTO testAstronautNormalDTO;
    private AstronautDetailedDTO testAstronautDetailedDTO;

    @BeforeEach
    void setUp() {
        this.testPageSortingDTO = PageSortingDTO.builder()
                .page(1)
                .limit(12)
                .field("name")
                .sort("desc")
                .build();

        this.testRequestParams = new HashMap<>();
        this.testRequestParams.put("search", "Yury Usachov");
        this.testRequestParams.put("nationality", "5");
        this.testRequestParams.put("status", "2");
        this.testRequestParams.put("agency", "44");

        this.testAstronaut = new Astronaut();
        this.testAstronaut.setAstronautId(274L);
        this.testAstronaut.setName("Yury Usachov");
        this.testAstronaut.setInSpace(false);

        this.testAstronautNormalDTO = new AstronautNormalDTO();
        this.testAstronautNormalDTO.setAstronautId(testAstronaut.getAstronautId());
        this.testAstronautNormalDTO.setName(testAstronaut.getName());
        this.testAstronautNormalDTO.setInSpace(testAstronaut.getInSpace());

        this.testAstronautDetailedDTO = new AstronautDetailedDTO();
        this.testAstronautDetailedDTO.setAstronautId(testAstronaut.getAstronautId());
        this.testAstronautDetailedDTO.setName(testAstronaut.getName());
        this.testAstronautDetailedDTO.setInSpace(testAstronaut.getInSpace());
    }

    @Nested
    @DisplayName("Search Astronaut By Filters")
    class SearchAstronautTest {

        @Test
        @DisplayName("Should Return Paged Astronaut Normal Summaries When No Filters Are Applied")
        void shouldReturnPagedAstronautsWithoutFilters() {

            // given: service method going to test
            Page<Astronaut> astroanutPage = new PageImpl<>(List.of(testAstronaut));

            when(astronautsRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(astroanutPage);

            when(dtoConverter.convertToDto(eq(testAstronaut), eq(AstronautNormalDTO.class)))
                    .thenReturn(testAstronautNormalDTO);

            // when: when test runs
            Page<DTOEntity> result =
                    astronautService.searchAstronaut(null, testPageSortingDTO);

            // then: result of test
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(testAstronautNormalDTO, result.getContent().getFirst());

            verify(astronautsRepository, times(1))
                    .findAll(any(Specification.class), any(Pageable.class));

            verify(dtoConverter, times(result.getSize()))
                    .convertToDto(testAstronaut, AstronautNormalDTO.class);
        }


        @Test
        @DisplayName("Should Return Paged Astronaut Normal Summaries When Filters Are Applied")
        void shouldReturnPagedLaunchesWithFilters() {

            // given: service method going to test
            Page<Astronaut> astroanutPage = new PageImpl<>(List.of(testAstronaut));

            when(astronautsRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(astroanutPage);

            when(dtoConverter.convertToDto(eq(testAstronaut), eq(AstronautNormalDTO.class)))
                    .thenReturn(testAstronautNormalDTO);

            // when: when test runs
            Page<DTOEntity> result =
                    astronautService.searchAstronaut(testRequestParams, testPageSortingDTO);

            // then: result of test
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertSame(testAstronautNormalDTO, result.getContent().getFirst());

            verify(astronautsRepository, times(1))
                    .findAll(any(Specification.class), any(Pageable.class));

            verify(dtoConverter, times(result.getSize()))
                    .convertToDto(testAstronaut, AstronautNormalDTO.class);
        }
    }

    @Nested
    @DisplayName("Find Astronaut By ID")
    class FindAstronautById {
        @Test
        @DisplayName("Should Return a Astronaut When ID Exists")
        void shouldReturnLaunchById() {

            // given
            Integer astronautId = 44;

            when(astronautsRepository.findAstronautByAstronautId(astronautId))
                    .thenReturn(Optional.of(testAstronaut));

            when(dtoConverter.convertToDto(testAstronaut, AstronautDetailedDTO.class))
                    .thenReturn(testAstronautDetailedDTO);

            // when
            DTOEntity result = astronautService.getAstronautById(astronautId);

            // then
            assertNotNull(result);
            assertEquals(testAstronautDetailedDTO, result);

            verify(astronautsRepository).findAstronautByAstronautId(astronautId);
            verify(dtoConverter).convertToDto(testAstronaut, AstronautDetailedDTO.class);
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Astronaut ID Is Null")
        void shouldHandleNullAstronautId() {

            // given
            final Integer astronautId = null;

            when(astronautsRepository.findAstronautByAstronautId(astronautId))
                    .thenReturn(Optional.empty());

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> astronautService.getAstronautById(astronautId));

            assertNotNull(exception);
            assertEquals("Astronaut not found with id: " + astronautId, exception.getMessage());
            verify(astronautsRepository, times(1)).findAstronautByAstronautId(astronautId);
            verifyNoInteractions(dtoConverter);
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Astronaut ID Not Found")
        void shouldThrowResourceNotFoundException() {

            // given
            final Integer astronautId = 123456;

            when(astronautsRepository.findAstronautByAstronautId(astronautId))
                    .thenReturn(Optional.empty());

            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> astronautService.getAstronautById(astronautId));

            assertNotNull(exception);
            assertEquals("Astronaut not found with id: " + astronautId, exception.getMessage());
            verify(astronautsRepository, times(1)).findAstronautByAstronautId(astronautId);
            verifyNoInteractions(dtoConverter);
        }
    }
}