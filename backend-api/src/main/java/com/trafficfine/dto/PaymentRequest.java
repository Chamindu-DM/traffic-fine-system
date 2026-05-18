package com.trafficfine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PaymentRequest(
        @NotBlank String referenceNumber,
        @NotBlank String categoryCode,
        @NotBlank String paymentMethod,
        @Pattern(regexp = "\\d{4}", message = "cardLastFourDigits must contain exactly four digits")
        String cardLastFourDigits
) {
}
