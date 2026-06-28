package com.trafficfine.fineservice.service;

import com.trafficfine.common.dto.CreateFineRequest;
import com.trafficfine.common.dto.CreateFineResponse;
import com.trafficfine.common.dto.FineLookupResponse;
import com.trafficfine.common.event.FineCreatedEvent;
import com.trafficfine.common.event.FineStatusChangedEvent;
import com.trafficfine.common.event.RabbitMQConstants;
import com.trafficfine.common.exception.BusinessRuleException;
import com.trafficfine.common.exception.ResourceNotFoundException;
import com.trafficfine.fineservice.entity.FineCategory;
import com.trafficfine.fineservice.entity.FineStatus;
import com.trafficfine.fineservice.entity.Officer;
import com.trafficfine.fineservice.entity.TrafficFine;
import com.trafficfine.fineservice.repository.FineCategoryRepository;
import com.trafficfine.fineservice.repository.OfficerRepository;
import com.trafficfine.fineservice.repository.TrafficFineRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FineService {

    private final TrafficFineRepository trafficFineRepository;
    private final FineCategoryRepository fineCategoryRepository;
    private final OfficerRepository officerRepository;
    private final RabbitTemplate rabbitTemplate;

    public FineService(
            TrafficFineRepository trafficFineRepository,
            FineCategoryRepository fineCategoryRepository,
            OfficerRepository officerRepository,
            RabbitTemplate rabbitTemplate
    ) {
        this.trafficFineRepository = trafficFineRepository;
        this.fineCategoryRepository = fineCategoryRepository;
        this.officerRepository = officerRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public CreateFineResponse createFine(CreateFineRequest request) {
        FineCategory category = fineCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Fine category not found"));

        Officer officer = officerRepository.findById(request.officerId())
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found"));

        String referenceNumber = generateUniqueReferenceNumber();

        TrafficFine fine = new TrafficFine(
                referenceNumber,
                category,
                officer,
                request.driverLicenseNumber(),
                request.vehicleNumber(),
                request.district(),
                FineStatus.UNPAID,
                LocalDateTime.now()
        );

        fine = trafficFineRepository.save(fine);

        // Publish Event
        FineCreatedEvent event = new FineCreatedEvent(
                fine.getReferenceNumber(),
                category.getCode(),
                fine.getAmount(),
                fine.getDistrict(),
                officer.getName(),
                fine.getIssuedAt()
        );
        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE, RabbitMQConstants.ROUTING_KEY_FINE_CREATED, event);

        return new CreateFineResponse(
                fine.getReferenceNumber(),
                fine.getAmount(),
                category.getCode(),
                fine.getStatus().name()
        );
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "fines", key = "#referenceNumber + '-' + #categoryCode")
    public FineLookupResponse lookupFine(String referenceNumber, String categoryCode) {
        TrafficFine fine = trafficFineRepository.findByReferenceNumberAndCategoryCodeIgnoreCase(referenceNumber, categoryCode)
                .orElseThrow(() -> new ResourceNotFoundException("Fine record not found for reference number: " + referenceNumber + " and category: " + categoryCode));

        return new FineLookupResponse(
                fine.getReferenceNumber(),
                fine.getCategory().getCode(),
                fine.getCategory().getName(),
                fine.getAmount(),
                fine.getDistrict(),
                fine.getOfficer().getName(),
                fine.getOfficer().getBadgeNumber(),
                fine.getOfficer().getPhoneNumber(),
                fine.getStatus().name(),
                fine.getIssuedAt()
        );
    }

    @Transactional(readOnly = true)
    public FineLookupResponse getFine(String referenceNumber) {
        TrafficFine fine = trafficFineRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Fine not found"));

        return new FineLookupResponse(
                fine.getReferenceNumber(),
                fine.getCategory().getCode(),
                fine.getCategory().getName(),
                fine.getAmount(),
                fine.getDistrict(),
                fine.getOfficer().getName(),
                fine.getOfficer().getBadgeNumber(),
                fine.getOfficer().getPhoneNumber(),
                fine.getStatus().name(),
                fine.getIssuedAt()
        );
    }

    @CacheEvict(value = "fines", allEntries = true)
    public void updateFineStatus(String referenceNumber, String status) {
        // Pessimistic Lock to prevent concurrent payment updates
        TrafficFine fine = trafficFineRepository.findByReferenceNumberForUpdate(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Fine not found"));

        FineStatus newStatus = FineStatus.valueOf(status.toUpperCase());
        if (fine.getStatus() == FineStatus.PAID) {
            throw new BusinessRuleException("Fine is already paid");
        }

        String oldStatus = fine.getStatus().name();
        fine.markPaid(LocalDateTime.now());
        trafficFineRepository.save(fine);

        // Publish Event
        FineStatusChangedEvent event = new FineStatusChangedEvent(
                fine.getReferenceNumber(),
                oldStatus,
                fine.getStatus().name(),
                fine.getPaidAt()
        );
        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE, RabbitMQConstants.ROUTING_KEY_FINE_STATUS_CHANGED, event);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "categories")
    public List<FineCategory> getAllCategories() {
        return fineCategoryRepository.findAll();
    }

    private String generateUniqueReferenceNumber() {
        return "TF" + UUID.randomUUID().toString()
                .replace("-", "").substring(0, 10).toUpperCase();
    }
}
