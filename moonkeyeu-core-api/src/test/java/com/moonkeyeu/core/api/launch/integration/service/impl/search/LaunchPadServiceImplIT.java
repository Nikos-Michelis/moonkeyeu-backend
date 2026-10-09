package com.moonkeyeu.core.api.launch.integration.service.impl.search;

import com.moonkeyeu.core.api.launch.config.TestContainerConfiguration;
import com.moonkeyeu.core.api.launch.config.TestSecurityConfiguration;
import com.moonkeyeu.core.api.launch.dto.pad.LaunchPadDTO;
import com.moonkeyeu.core.api.launch.dto.pad.LaunchPadDetailedDTO;
import com.moonkeyeu.core.api.launch.model.pad.LaunchPad;
import com.moonkeyeu.core.api.launch.services.impl.search.LaunchPadServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
@Import({TestSecurityConfiguration.class, TestContainerConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("LaunchPadServiceImplIntTest Integration Tests")
@Transactional
class LaunchPadServiceImplIT {
    @Autowired
    private LaunchPadServiceImpl launchPadService;

    @Test
    @DisplayName("Should Return All LaunchPads Grouped By Active Status")
    void shouldReturnAllLaunchPads() {
        Map<String, Object> result = launchPadService.getAllLaunchPads();
        int activeCount = (int) result.get("active");
        int inactiveCount = (int) result.get("inactive");
        List<LaunchPadDTO> pads = (List<LaunchPadDTO>) result.get("pads");
        assertTrue(activeCount >= 100);
        assertTrue(inactiveCount >= 15);
        assertTrue( pads.size() >= 100);
    }

    @Nested
    @DisplayName("Find LaunchPad By ID")
    class FindLaunchPadById {
        @Test
        @DisplayName("Should Return a LaunchPad When ID Exists")
        void shouldReturnLaunchPadById() {
            // given
            Integer launchPadId = 87;
            // when
            LaunchPadDetailedDTO result = (LaunchPadDetailedDTO) launchPadService.getLaunchPadById(launchPadId);
            // then
            assertNotNull(result);
            assertEquals(launchPadId, result.getLaunchPadId().intValue());
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When LaunchPad ID Is Null")
        void shouldHandleNullLaunchPadId() {
            // given
            Integer launchPadId = null;
            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> launchPadService.getLaunchPadById(launchPadId));

            assertNotNull(exception);
            assertEquals("LaunchPad not found with id: " + launchPadId, exception.getMessage());
        }

        @Test
        @DisplayName("Should Throw ResourceNotFoundException When LaunchPad ID Not Found")
        void shouldThrowResourceNotFoundException() {
            // given
            Integer launchPadId = 13234;
            // when & Then
            final ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    ()-> launchPadService.getLaunchPadById(launchPadId));

            assertNotNull(exception);
            assertEquals("LaunchPad not found with id: " + launchPadId, exception.getMessage());
        }
    }
}