package com.trafficfine.common.dto;

import java.math.BigDecimal;

public record CreateFineResponse(
        String referenceNumber,
        BigDecimal amount,
        String categoryCode,
        String status
) {}
