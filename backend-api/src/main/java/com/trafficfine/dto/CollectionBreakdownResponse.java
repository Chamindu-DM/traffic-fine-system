package com.trafficfine.dto;

import java.math.BigDecimal;

public record CollectionBreakdownResponse(
        String label,
        BigDecimal amount
) {
}
