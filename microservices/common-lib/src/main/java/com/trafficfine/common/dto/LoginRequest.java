package com.trafficfine.common.dto;

public record LoginRequest(
        String username,
        String password
) {}
