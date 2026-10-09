package com.moonkeyeu.core.api.configuration.agent;

import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserAgentAnalyzerConfig {
    private static final int CACHE_SIZE = 1000;

    @Bean
    public UserAgentAnalyzer userAgentAnalyzer() {
        return nl.basjes.parse.useragent.UserAgentAnalyzer.newBuilder()
                .hideMatcherLoadStats()
                .withCache(CACHE_SIZE)
                .build();
    }
}
