package com.trafficfine.dto;

public record OfficerDto(
        Long id,
        String name,
        String badgeNumber,
        String district
) {
}
