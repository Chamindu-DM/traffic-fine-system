package com.trafficfine.common.dto;

public record LoginResponse(
        String token,
        String role,
        long expiresIn
) {}
