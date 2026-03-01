package com.moonkeyeu.core.api.launch.config;

import com.moonkeyeu.core.api.configuration.security.jwt.JwtKeyConfig;
import com.moonkeyeu.core.api.security.services.AuthenticationService;
import com.moonkeyeu.core.api.security.services.jwt.JwtServiceParserImpl;
import com.moonkeyeu.core.api.security.services.jwt.JwtServiceProvider;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public JwtKeyConfig jwtKeyConfig() {
        return Mockito.mock(JwtKeyConfig.class);
    }

    @Bean
    @Primary
    public AuthenticationService authenticationService() {
        return Mockito.mock(AuthenticationService.class);
    }

    @Bean
    @Primary
    public JwtServiceParserImpl jwtServiceParserImpl() {
        return Mockito.mock(JwtServiceParserImpl.class);
    }

    @Bean
    @Primary
    public JwtServiceProvider jwtServiceProvider() {
        return Mockito.mock(JwtServiceProvider.class);
    }
}