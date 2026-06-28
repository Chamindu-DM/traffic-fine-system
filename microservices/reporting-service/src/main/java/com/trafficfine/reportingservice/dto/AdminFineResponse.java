package com.trafficfine.reportingservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminFineResponse(
        String referenceNumber,
        String category,
        String district,
        BigDecimal amount,
        String status,
        LocalDateTime issuedAt
) {}
