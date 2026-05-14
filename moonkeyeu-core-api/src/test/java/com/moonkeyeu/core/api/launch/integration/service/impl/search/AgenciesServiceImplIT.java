package com.moonkeyeu.core.api.launch.integration.service.impl.search;

import com.moonkeyeu.core.api.launch.config.TestContainerConfiguration;
import com.moonkeyeu.core.api.launch.config.TestSecurityConfiguration;
import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.agency.AgencyDetailedDTO;
import com.moonkeyeu.core.api.launch.services.impl.search.AgenciesServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestSecurityConfiguration.class, TestContainerConfiguration.class})
@DisplayName("AgenciesServiceImplTest Integration Tests")
@Transactional
class AgenciesServiceImplIT {
    
    @Autowired
    private AgenciesServiceImpl agenciesService;

    @Test
    @DisplayName("Should Return All Agencies Marked as Featured")
    void shouldReturnAllFeaturedAgencies() {
        List<DTOEntity> result = agenciesService.getAllAgencies();
        assertNotNull(result);
        assertEquals(15, result.size());
    }

    @Nested
    @DisplayName("Find Agency By ID")
    class FindAgencyByIdTests {
        @Test
        @DisplayName("Should Return an Agency Configuration When ID Exists")
        void shouldReturnAgencyById() {
            Integer agencyId = 44;
            AgencyDetailedDTO result = (AgencyDetailedDTO) agenciesService.getAgencyById(agencyId);
            assertNotNull(result);
            assertEquals(44, result.getAgencyId());
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Agency ID Is Null")
        void shouldHandleNullAgencyId() {
            // given
            Integer agencyId = null;
            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> agenciesService.getAgencyById(agencyId));
            assertNotNull(exception);
            assertEquals("Agency not found with id: " + agencyId, exception.getMessage());
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When Agency ID Not Found")
        void shouldThrowResourceNotFoundException() {
            // given
            Integer agencyId = 123456;
            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> agenciesService.getAgencyById(agencyId));
            assertNotNull(exception);
            assertEquals("Agency not found with id: " + agencyId, exception.getMessage());
        }
    }
}