package com.trafficfine.common.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        String paymentReference,
        String referenceNumber,
        BigDecimal amount,
        String status,
        LocalDateTime paidAt,
        String message
) {}
