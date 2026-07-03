package com.trafficfine.dto;

import java.math.BigDecimal;

public record FineCategoryDto(
        Long id,
        String code,
        String name,
        BigDecimal defaultAmount
) {
}
