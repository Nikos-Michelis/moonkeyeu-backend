package com.moonkeyeu.etl.api.service;

import com.moonkeyeu.etl.api.dto.LL2Throttle;
import reactor.core.publisher.Mono;

public interface ClientThrottleService {
    Mono<LL2Throttle> fetchThrottle();
}
