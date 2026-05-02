package com.moonkeyeu.etl.api.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Configuration
@RequiredArgsConstructor
public class UrlBuilderConfig {
    @Value("${application.api.the-space-devs.url}")
    private String baseUrl;
    @Value("${application.api.the-space-devs.version}")
    private String version;

    private UriComponentsBuilder baseLaunchesUriBuilder() {
        return UriComponentsBuilder
                .fromUri(URI.create(baseUrl))
                .pathSegment(version, "launches")
                .path("/")
                .queryParam("format=json")
                .queryParam("mode", "detailed")
                .queryParam("limit", 100);
    }

    public UriComponentsBuilder baseAgenciesUriBuilder() {
        return UriComponentsBuilder
                .fromUri(URI.create(baseUrl))
                .pathSegment(version, "agencies")
                .path("/")
                .queryParam("format=json")
                .queryParam("mode", "detailed")
                .queryParam("limit", 100);
    }

    public URI baseAgenciesUrl() {
        return baseAgenciesUriBuilder()
                .build()
                .toUri();
    }

    public URI baseLaunchesUrl() {
        return baseLaunchesUriBuilder()
                .build()
                .toUri();
    }

    public URI getAllLatestLaunchesUrl() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        String windowStart = now.toLocalDate().minusMonths(1).toString();
        return baseLaunchesUriBuilder()
                .queryParam("ordering", "-last_updated")
                .queryParam("net__gte", windowStart)
                .build()
                .toUri();
    }

    public URI getLaunchesUrlForWindow() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        String windowStart = now.toLocalDate().minusMonths(1).toString();
        String windowEnd = now.plusMonths(7).toLocalDate().toString();
        return baseLaunchesUriBuilder()
                .queryParam("ordering", "-last_updated")
                .queryParam("net__gte", windowStart)
                .queryParam("net__lte", windowEnd)
                .build()
                .toUri();
    }
}
