package com.moonkeyeu.core.api.launch.integration.repository;

import com.moonkeyeu.core.api.launch.model.pad.LaunchPad;
import com.moonkeyeu.core.api.launch.repository.LaunchPadRepository;
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
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LaunchPadRepositoryTest {
    @Container
    @ServiceConnection
    static MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>("mysql:8.0.30")
                    .withDatabaseName("moonkey_db");
    @Autowired
    private LaunchPadRepository launchPadRepository;

    @Test
    void connectionEstablished() {
        assertThat(mysqlContainer.isCreated()).isTrue();
        assertThat(mysqlContainer.isRunning()).isTrue();
    }

    @Test
    void ShouldFindLaunchPadById() {
        Optional<LaunchPad> launchPad = launchPadRepository.findLaunchPadWithPadId(44);
        assertTrue(launchPad.isPresent());
    }

    @Test
    void ShouldFindAllLaunchPads() {
        List<LaunchPad> launchPads = launchPadRepository.findAll();
        assertFalse(launchPads.isEmpty());
    }

}