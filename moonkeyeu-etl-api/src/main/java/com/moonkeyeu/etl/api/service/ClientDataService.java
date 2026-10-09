package com.moonkeyeu.etl.api.service;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

import java.net.URI;

public interface ClientDataService {
    Mono<Void> fetchAll(URI url, String fileName);
    Mono<Void> fetchNext(URI url, String fileName, JsonGenerator generator);
    Mono<JsonNode> fetch(URI url);
}