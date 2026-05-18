package com.trafficfine.dto;

public record LoginResponse(
        String token,
        String role
) {
}
