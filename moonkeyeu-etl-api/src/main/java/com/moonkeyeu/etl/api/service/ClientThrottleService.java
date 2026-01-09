package com.moonkeyeu.etl.api.service;

import reactor.core.publisher.Mono;

public interface ClientThrottleService {
    Mono<Long> getThrottleDelay();
}
