package com.moonkeyeu.core.api.launch.config;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestContainerConfig {
    @Container
    @ServiceConnection
    static MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>("mysql:8.0.30")
                    .withDatabaseName("moonkey_db")
                    .withExposedPorts(3306)
                    .withPassword("test");

    @DynamicPropertySource
    static void properties(final DynamicPropertyRegistry registry) {

        mysqlContainer.start();

        registry.add("spring.datasource.url", () -> String.format(
                "jdbc:mysql:/%s:%d/%s",
                mysqlContainer.getHost(),
                mysqlContainer.getMappedPort(3306),
                mysqlContainer.getDatabaseName()));
        registry.add("spring.datasource.username", () -> mysqlContainer.getUsername());
        registry.add("spring.datasource.password", () -> mysqlContainer.getPassword());
    }

}
