package com.moonkeyeu.core.api.launch.services;

import com.moonkeyeu.core.api.launch.dto.NewsDTO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ClientNewsService {
    Mono<List<NewsDTO>> fetchLatestNewsByLaunchId(String launchId);
}
