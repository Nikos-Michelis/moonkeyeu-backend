package com.moonkeyeu.core.api.launch.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfiguration {

    @Bean
    @ServiceConnection
    @SuppressWarnings("resource")
    public MySQLContainer<?> mysqlContainer() {
        return new MySQLContainer<>("mysql:8.0.30")
                .withDatabaseName("moonkey_db")
                .withUsername("test")
                .withPassword("test");
    }
}