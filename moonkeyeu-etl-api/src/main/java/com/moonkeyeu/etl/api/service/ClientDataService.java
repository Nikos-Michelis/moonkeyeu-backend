package com.moonkeyeu.etl.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

public interface ClientDataService {
    Mono<Void> fetchData(URI url, String fileName);
    Mono<JsonNode> fetchThrottle(URI url, List<JsonNode> allResults);
    Mono<JsonNode> fetchNextPage(URI url, List<JsonNode> allResults);
}
