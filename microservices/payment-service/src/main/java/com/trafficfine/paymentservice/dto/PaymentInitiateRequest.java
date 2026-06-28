package com.trafficfine.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentInitiateRequest(
        @NotBlank(message = "Reference number is required")
        String referenceNumber,

        @NotBlank(message = "Category code is required")
        String categoryCode
) {}
