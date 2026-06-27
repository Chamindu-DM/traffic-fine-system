package com.trafficfine.common.dto;

public record PaymentRequest(
        String referenceNumber,
        String categoryCode,
        String paymentMethod,
        String cardLastFourDigits
) {}
