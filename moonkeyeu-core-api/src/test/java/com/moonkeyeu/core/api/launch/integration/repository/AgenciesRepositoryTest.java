package com.moonkeyeu.core.api.launch.integration.repository;

import com.moonkeyeu.core.api.launch.model.agency.Agencies;
import com.moonkeyeu.core.api.launch.repository.AgenciesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AgenciesRepositoryTest {
    @Container
    @ServiceConnection
    static MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>("mysql:8.0.30")
                    .withDatabaseName("moonkey_db");
    @Autowired
    private AgenciesRepository agenciesRepository;

    @Test
    void connectionEstablished() {
        assertThat(mysqlContainer.isCreated()).isTrue();
        assertThat(mysqlContainer.isRunning()).isTrue();
    }

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