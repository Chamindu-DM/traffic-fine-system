package com.trafficfine.service;

import com.trafficfine.dto.FineLookupResponse;
import com.trafficfine.entity.TrafficFine;
import com.trafficfine.repository.TrafficFineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FineService {

    private final TrafficFineRepository trafficFineRepository;
    private final FineMapper fineMapper;

    public FineService(TrafficFineRepository trafficFineRepository, FineMapper fineMapper) {
        this.trafficFineRepository = trafficFineRepository;
        this.fineMapper = fineMapper;
    }

    @Transactional(readOnly = true)
    public FineLookupResponse lookup(String referenceNumber, String categoryCode) {
        return fineMapper.toLookupResponse(findFine(referenceNumber, categoryCode));
    }

    public TrafficFine findFine(String referenceNumber, String categoryCode) {
        return trafficFineRepository.findByReferenceNumberAndCategoryCodeIgnoreCase(referenceNumber, categoryCode)
                .orElseThrow(() -> new ResourceNotFoundException("Fine not found for the provided reference number and category code"));
    }
}
