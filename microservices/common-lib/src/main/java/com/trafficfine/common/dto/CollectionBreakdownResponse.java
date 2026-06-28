package com.trafficfine.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record CollectionBreakdownResponse(
        String label,
        BigDecimal amount
) implements Serializable {}
