package com.moonkeyeu.core.api.launch.integration.service.impl.search;

import com.moonkeyeu.core.api.launch.config.TestContainerConfig;
import com.moonkeyeu.core.api.launch.config.TestSecurityConfig;
import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.launch.dto.program.ProgramDetailedDTO;
import com.moonkeyeu.core.api.launch.services.impl.search.ProgramsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
@Import(TestSecurityConfig.class)
@Transactional
@DisplayName("ProgramsServiceImplIntTest Integration Tests")
class ProgramsServiceImplIntTest extends TestContainerConfig {

    @Autowired
    private ProgramsServiceImpl programsService;
    private PageSortingDTO testPageSortingDTO;
    private Map<String, String> testRequestParams;
    @BeforeEach
    void setUp() {
        this.testPageSortingDTO = PageSortingDTO.builder()
                .page(0)
                .limit(12)
                .field("startDate")
                .sort("asc")
                .build();

        this.testRequestParams = new HashMap<>();
        this.testRequestParams.put("search", "Space Shuttle");
    }

    @Nested
    @DisplayName("Search Programs by Filters")
    class SerachAstronautTests {

        @Test
        @DisplayName("Should Return Paged Programs Summaries When No Filters Are Applied")
        void shouldReturnPagedProgramsWithoutFilters() {
            // when
            Page<DTOEntity> result = programsService.searchProgram(Collections.emptyMap(), testPageSortingDTO);
            // then
            assertNotNull(result);
            assertEquals(23, result.getTotalElements());
        }

        @Test
        @DisplayName("Should Return Paged Programs Summaries When Filters Are Applied")
        void shouldReturnPagedProgramsWithFilters() {
            // when
            Page<DTOEntity> result = programsService.searchProgram(testRequestParams, testPageSortingDTO);
            // then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Find Program By ID")
    class findProgramById {
        @Test
        @DisplayName("Should Return a Program When ID Exists")
        void shouldReturnProgramById() {
            // given
            Integer programId = 6;
            // when
            ProgramDetailedDTO result = (ProgramDetailedDTO) programsService.getProgramById(programId);
            // then
            assertNotNull(result);
            assertEquals(6, result.getProgramId());
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Program ID Is Null")
        void shouldHandleNullProgramId() {
            // given
            Integer programId = null;

            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> programsService.getProgramById(programId));

            assertNotNull(exception);
            assertEquals("Program not found with id: " + programId, exception.getMessage());
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Program ID Not Found")
        void shouldThrowResourceNotFoundException() {
            // given
            final Integer programId = 123456;
            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> programsService.getProgramById(programId));

            assertNotNull(exception);
            assertEquals("Program not found with id: " + programId, exception.getMessage());
        }
    }
}