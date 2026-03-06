package com.moonkeyeu.core.api.launch.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.reactive.server.WebTestClient;

@TestConfiguration
@Profile("test")
public class WebTestClientConfiguration {

    @Bean
    public WebTestClient webTestClient(ApplicationContext applicationContext) {
        return WebTestClient.bindToApplicationContext(applicationContext).build();
    }
}