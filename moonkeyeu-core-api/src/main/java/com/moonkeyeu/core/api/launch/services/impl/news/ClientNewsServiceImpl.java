package com.moonkeyeu.core.api.launch.services.impl.news;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moonkeyeu.core.api.launch.dto.NewsDTO;
import com.moonkeyeu.core.api.launch.services.ClientNewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class ClientNewsServiceImpl implements ClientNewsService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ClientNewsServiceImpl(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            @Value("${application.api.news.url}") String url,
            @Value("${application.api.news.version}") String version
    ) {
        this.webClient = webClientBuilder
                .baseUrl(url + version)
                .build();
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<List<NewsDTO>> fetchLatestNewsByLaunchId(String launchId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/articles/")
                        .queryParam("format", "json")
                        .queryParam("launch", launchId)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> {
                    JsonNode results = jsonNode.get("results");
                    if (results != null && results.isArray()) {
                        return StreamSupport.stream(results.spliterator(), false)
                                .map(node -> {
                                    try {
                                        return objectMapper.treeToValue(node, NewsDTO.class);
                                    } catch (JsonProcessingException e) {
                                        log.error("Error mapping NewsDTO", e);
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull)
                                .toList();
                    } else {
                        return Collections.emptyList();
                    }
                });
    }
}
