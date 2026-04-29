package com.moonkeyeu.core.api.membership.payment.dto;

import java.math.BigDecimal;

public record PaymentResponseDTO(String sessionId, String sessionUrl, String Status) {
}
