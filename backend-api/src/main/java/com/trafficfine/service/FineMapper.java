package com.trafficfine.service;

import com.trafficfine.dto.AdminFineResponse;
import com.trafficfine.dto.FineLookupResponse;
import com.trafficfine.entity.TrafficFine;
import org.springframework.stereotype.Component;

@Component
public class FineMapper {

    public FineLookupResponse toLookupResponse(TrafficFine fine) {
        return new FineLookupResponse(
                fine.getReferenceNumber(),
                fine.getCategory().getCode(),
                fine.getCategory().getName(),
                fine.getAmount(),
                fine.getDistrict(),
                fine.getOfficer().getName(),
                fine.getStatus().name(),
                fine.getIssuedAt(),
                fine.getPaidAt()
        );
    }

    public AdminFineResponse toAdminResponse(TrafficFine fine) {
        return new AdminFineResponse(
                fine.getReferenceNumber(),
                fine.getCategory().getCode(),
                fine.getCategory().getName(),
                fine.getDistrict(),
                fine.getOfficer().getName(),
                fine.getDriverLicenseNumber(),
                fine.getVehicleNumber(),
                fine.getAmount(),
                fine.getStatus().name(),
                fine.getIssuedAt(),
                fine.getPaidAt()
        );
    }
}
