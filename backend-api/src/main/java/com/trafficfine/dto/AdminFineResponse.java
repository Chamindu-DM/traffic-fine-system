package com.trafficfine.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminFineResponse(
        String referenceNumber,
        String categoryCode,
        String category,
        String district,
        String officer,
        String driverLicenseNumber,
        String vehicleNumber,
        BigDecimal amount,
        String status,
        LocalDateTime issuedAt,
        LocalDateTime paidAt
) {
}
