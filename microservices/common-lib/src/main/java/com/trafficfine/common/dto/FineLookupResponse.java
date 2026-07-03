package com.trafficfine.common.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FineLookupResponse(
        String referenceNumber,
        String categoryCode,
        String categoryName,
        BigDecimal amount,
        String district,
        String officerName,
        String officerBadgeNumber,
        String status,
        LocalDateTime issuedAt
) {}
