package com.trafficfine.common.dto;

import java.math.BigDecimal;

public record CollectionBreakdownResponse(
        String label,
        BigDecimal amount
) {}
