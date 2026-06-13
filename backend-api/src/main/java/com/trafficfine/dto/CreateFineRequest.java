package com.trafficfine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateFineRequest(
        @NotBlank(message = "Driver license number must not be blank")
        String driverLicenseNumber,

        @NotBlank(message = "Vehicle number must not be blank")
        String vehicleNumber,

        @NotBlank(message = "District must not be blank")
        String district,

        @NotNull(message = "Category ID must not be null")
        Long categoryId,

        @NotNull(message = "Officer ID must not be null")
        Long officerId
) {
}
