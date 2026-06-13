package com.trafficfine.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentInitiateRequest(
        @NotBlank String referenceNumber,
        @NotBlank String categoryCode
) {
}
