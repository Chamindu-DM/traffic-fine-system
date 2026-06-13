package com.trafficfine.service;

import com.trafficfine.dto.CreateFineRequest;
import com.trafficfine.dto.CreateFineResponse;
import com.trafficfine.dto.FineLookupResponse;
import com.trafficfine.entity.FineCategory;
import com.trafficfine.entity.FineStatus;
import com.trafficfine.entity.Officer;
import com.trafficfine.entity.TrafficFine;
import com.trafficfine.repository.FineCategoryRepository;
import com.trafficfine.repository.OfficerRepository;
import com.trafficfine.repository.TrafficFineRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FineService {

    private final TrafficFineRepository trafficFineRepository;
    private final FineCategoryRepository fineCategoryRepository;
    private final OfficerRepository officerRepository;
    private final FineMapper fineMapper;

    public FineService(
            TrafficFineRepository trafficFineRepository,
            FineCategoryRepository fineCategoryRepository,
            OfficerRepository officerRepository,
            FineMapper fineMapper
    ) {
        this.trafficFineRepository = trafficFineRepository;
        this.fineCategoryRepository = fineCategoryRepository;
        this.officerRepository = officerRepository;
        this.fineMapper = fineMapper;
    }

    @Transactional(readOnly = true)
    public FineLookupResponse lookup(String referenceNumber, String categoryCode) {
        return fineMapper.toLookupResponse(findFine(referenceNumber, categoryCode));
    }

    @Transactional(readOnly = true)
    public TrafficFine findFine(String referenceNumber, String categoryCode) {
        return trafficFineRepository.findByReferenceNumberAndCategoryCodeIgnoreCase(referenceNumber, categoryCode)
                .orElseThrow(() -> new ResourceNotFoundException("Fine not found for the provided reference number and category code"));
    }

    @Transactional
    public CreateFineResponse create(CreateFineRequest request) {
        FineCategory category = fineCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Fine category not found with ID: " + request.categoryId()));
        Officer officer = officerRepository.findById(request.officerId())
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found with ID: " + request.officerId()));

        String refNum = generateUniqueReferenceNumber();
        TrafficFine fine = new TrafficFine(
                refNum,
                category,
                officer,
                request.driverLicenseNumber(),
                request.vehicleNumber(),
                request.district(),
                FineStatus.UNPAID,
                LocalDateTime.now()
        );

        TrafficFine savedFine = trafficFineRepository.save(fine);
        return new CreateFineResponse(
                savedFine.getReferenceNumber(),
                savedFine.getAmount(),
                category.getCode(),
                savedFine.getStatus().name()
        );
    }

    private String generateUniqueReferenceNumber() {
        java.util.Random random = new java.util.Random();
        String refNum;
        do {
            int number = random.nextInt(1000000);
            refNum = "TF" + String.format("%06d", number);
        } while (trafficFineRepository.existsByReferenceNumber(refNum));
        return refNum;
    }
}
