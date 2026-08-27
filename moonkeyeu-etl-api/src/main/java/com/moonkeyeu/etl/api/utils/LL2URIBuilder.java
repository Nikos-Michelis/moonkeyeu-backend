package com.moonkeyeu.etl.api.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class LL2URIBuilder {
    private static final int PAGE_LIMIT = 100;
    @Value("${application.api.the-space-devs.url}")
    private String baseUrl;
    @Value("${application.api.the-space-devs.version}")
    private String version;


    private UriComponentsBuilder base(String resource) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment(version, resource)
                .path("/")
                .queryParam("format", "json");
    }

    private UriComponentsBuilder collection(String resource, int limit) {
        return base(resource)
                .queryParam("mode", "detailed")
                .queryParam("limit", limit);
    }

    public URI launchesFromURI(LocalDate from) {
        return collection("launches", PAGE_LIMIT)
                .queryParam("ordering", "-last_updated")
                .queryParam("net__gte", from)
                .build()
                .toUri();
    }

    public URI launchesBetweenURI(LocalDate from, LocalDate to) {
        return collection("launches", PAGE_LIMIT)
                .queryParam("ordering", "-last_updated")
                .queryParam("net__gte", from)
                .queryParam("net__lte", to)
                .build()
                .toUri();
    }

    public URI allAgenciesURI() {
        return collection("agencies", PAGE_LIMIT).build().toUri();
    }

    public URI throttleURI() {
        return base("api-throttle").build().toUri();
    }
}
