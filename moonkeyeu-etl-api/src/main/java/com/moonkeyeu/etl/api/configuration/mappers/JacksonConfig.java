package com.moonkeyeu.etl.api.configuration.mappers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        JsonFactory jsonFactory = new JsonFactory();
        return new ObjectMapper(jsonFactory);
    }
}
