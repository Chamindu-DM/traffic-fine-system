package com.trafficfine.common.dto;

public record CreateFineRequest(
        Long categoryId,
        Long officerId,
        String driverLicenseNumber,
        String vehicleNumber,
        String district
) {}
