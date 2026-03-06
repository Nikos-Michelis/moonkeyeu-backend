package com.moonkeyeu.core.api.launch.integration.repository;

import com.moonkeyeu.core.api.launch.config.TestContainerConfiguration;
import com.moonkeyeu.core.api.launch.model.agency.Agencies;
import com.moonkeyeu.core.api.launch.repository.AgenciesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AgenciesRepositoryIT {
    @Autowired
    private AgenciesRepository agenciesRepository;

    @Test
    void ShouldFindAgencyById() {
        Optional<Agencies> agencies = agenciesRepository.findAgencyById(44);
        assertTrue(agencies.isPresent());
    }

    @Test
    @DisplayName("Should return all agencies with featured tag")
    void ShouldFindAllAgencies() {
        List<Agencies> agencies = agenciesRepository.findAll();
        assertFalse(agencies.isEmpty());
    }
}