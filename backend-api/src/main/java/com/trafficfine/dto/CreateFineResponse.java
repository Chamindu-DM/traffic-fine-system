package com.trafficfine.dto;

import java.math.BigDecimal;

public record CreateFineResponse(
        String referenceNumber,
        BigDecimal amount,
        String category,
        String status
) {
}
