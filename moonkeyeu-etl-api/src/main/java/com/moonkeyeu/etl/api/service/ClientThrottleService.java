package com.moonkeyeu.etl.api.service;

import com.moonkeyeu.etl.api.dto.ThrottleResponse;
import reactor.core.publisher.Mono;

public interface ClientThrottleService {
    Mono<ThrottleResponse> fetchThrottle();
}
