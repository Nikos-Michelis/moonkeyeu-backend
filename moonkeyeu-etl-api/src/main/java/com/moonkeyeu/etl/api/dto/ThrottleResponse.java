package com.moonkeyeu.etl.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ThrottleResponse(
        @JsonProperty("your_request_limit") int requestLimit,
        @JsonProperty("current_use") int currentUse,
        @JsonProperty("next_use_secs") long nextUseSeconds,
        @JsonProperty("limit_frequency_secs") long limitFrequencySeconds,
        String ident
) {

    public ThrottleResponse {
        nextUseSeconds = Math.abs(nextUseSeconds);
    }
}