package com.trafficfine.service;

import com.trafficfine.dto.PaymentRequest;
import com.trafficfine.dto.PaymentResponse;
import com.trafficfine.entity.FineStatus;
import com.trafficfine.entity.Payment;
import com.trafficfine.entity.PaymentStatus;
import com.trafficfine.entity.TrafficFine;
import com.trafficfine.integration.MockPaymentGateway;
import com.trafficfine.integration.MockPaymentGateway.PaymentGatewayResult;
import com.trafficfine.repository.PaymentRepository;
import com.trafficfine.repository.TrafficFineRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final FineService fineService;
    private final TrafficFineRepository trafficFineRepository;
    private final PaymentRepository paymentRepository;
    private final MockPaymentGateway paymentGateway;
    private final SmsService smsService;

    public PaymentService(
            FineService fineService,
            TrafficFineRepository trafficFineRepository,
            PaymentRepository paymentRepository,
            MockPaymentGateway paymentGateway,
            SmsService smsService) {
        this.fineService = fineService;
        this.trafficFineRepository = trafficFineRepository;
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
        this.smsService = smsService;
    }

    @Transactional
    public PaymentResponse pay(PaymentRequest request) {
        TrafficFine fine = fineService.findFine(request.referenceNumber(), request.categoryCode());

        if (fine.getStatus() == FineStatus.PAID) {
            throw new BusinessRuleException("This fine has already been paid");
        }
        if (fine.getStatus() == FineStatus.CANCELLED) {
            throw new BusinessRuleException("Cancelled fines cannot be paid");
        }

        PaymentGatewayResult gatewayResult = paymentGateway.charge(request);
        if (!gatewayResult.successful()) {
            throw new BusinessRuleException(gatewayResult.message());
        }

        LocalDateTime paidAt = LocalDateTime.now();
        Payment payment = paymentRepository.save(new Payment(
                fine,
                gatewayResult.paymentReference(),
                fine.getAmount(),
                request.paymentMethod().trim().toUpperCase(),
                PaymentStatus.SUCCESS,
                paidAt));
        fine.markPaid(paidAt);
        trafficFineRepository.save(fine);
        smsService.sendPaymentConfirmation(fine);

        return new PaymentResponse(
                payment.getPaymentReference(),
                fine.getReferenceNumber(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getPaidAt(),
                "Payment successful. SMS notification processed for the traffic police officer.");
    }
}
