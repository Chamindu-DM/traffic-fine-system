package com.trafficfine.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record PaymentInitiateRequest(
        @JsonProperty("referenceNumber")
        @NotBlank(message = "Reference number is required")
        String referenceNumber,

        @JsonProperty("categoryCode")
        @NotBlank(message = "Category code is required")
        String categoryCode
) {}
