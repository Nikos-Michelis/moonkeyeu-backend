package com.moonkeyeu.core.api.launch.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({ "id", "completion", "updatedAt" })
public class AiResponseDTO {
    private Object id;
    private String completion;
    private Instant updatedAt;
}
