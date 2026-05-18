package com.trafficfine.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FineLookupResponse(
        String referenceNumber,
        String categoryCode,
        String category,
        BigDecimal amount,
        String district,
        String officer,
        String status,
        LocalDateTime issuedAt,
        LocalDateTime paidAt
) {
}
