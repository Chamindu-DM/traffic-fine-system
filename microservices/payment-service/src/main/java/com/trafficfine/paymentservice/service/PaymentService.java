package com.trafficfine.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trafficfine.common.dto.FineLookupResponse;
import com.trafficfine.common.dto.PaymentRequest;
import com.trafficfine.common.dto.PaymentResponse;
import com.trafficfine.common.event.PaymentCompletedEvent;
import com.trafficfine.common.event.RabbitMQConstants;
import com.trafficfine.common.exception.BusinessRuleException;
import com.trafficfine.paymentservice.client.FineClient;
import com.trafficfine.paymentservice.entity.Payment;
import com.trafficfine.paymentservice.entity.PaymentStatus;
import com.trafficfine.paymentservice.integration.MockPaymentGateway;
import com.trafficfine.paymentservice.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentService {

    private final FineClient fineClient;
    private final PaymentRepository paymentRepository;
    private final MockPaymentGateway paymentGateway;
    private final RabbitTemplate rabbitTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public PaymentService(
            FineClient fineClient,
            PaymentRepository paymentRepository,
            MockPaymentGateway paymentGateway,
            RabbitTemplate rabbitTemplate,
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper
    ) {
        this.fineClient = fineClient;
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
        this.rabbitTemplate = rabbitTemplate;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public PaymentResponse pay(PaymentRequest request, String idempotencyKey) {
        // Idempotency Check
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            String cachedResponse = redisTemplate.opsForValue().get("idempotency:" + idempotencyKey);
            if (cachedResponse != null) {
                try {
                    return objectMapper.readValue(cachedResponse, PaymentResponse.class);
                } catch (Exception e) {
                    // Fall through if serialization failed
                }
            }
        }

        FineLookupResponse fine = fineClient.getFine(request.referenceNumber());
        if ("PAID".equalsIgnoreCase(fine.status())) {
            throw new BusinessRuleException("This fine has already been paid");
        }
        if ("CANCELLED".equalsIgnoreCase(fine.status())) {
            throw new BusinessRuleException("Cancelled fines cannot be paid");
        }

        // Charge Mock Payment Gateway
        MockPaymentGateway.PaymentGatewayResult gatewayResult = paymentGateway.charge(request);
        if (!gatewayResult.successful()) {
            throw new BusinessRuleException(gatewayResult.message());
        }

        LocalDateTime paidAt = LocalDateTime.now();
        Payment payment = new Payment(
                fine.referenceNumber(),
                gatewayResult.paymentReference(),
                fine.amount(),
                request.paymentMethod().trim().toUpperCase(),
                PaymentStatus.SUCCESS,
                paidAt,
                idempotencyKey
        );

        payment = paymentRepository.save(payment);

        // Update Fine Service Status
        fineClient.updateFineStatus(fine.referenceNumber(), "PAID");

        // Publish event to RabbitMQ
        PaymentCompletedEvent event = new PaymentCompletedEvent(
                payment.getPaymentReference(),
                fine.referenceNumber(),
                payment.getAmount(),
                fine.officerPhoneNumber(),
                fine.officerName(),
                payment.getPaidAt()
        );
        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE, RabbitMQConstants.ROUTING_KEY_PAYMENT_COMPLETED, event);

        PaymentResponse response = new PaymentResponse(
                payment.getPaymentReference(),
                fine.referenceNumber(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getPaidAt(),
                "Payment successful. SMS notification processed for the traffic police officer."
        );

        // Store in Redis cache for 24 hours to enforce idempotency
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            try {
                String jsonResponse = objectMapper.writeValueAsString(response);
                redisTemplate.opsForValue().set("idempotency:" + idempotencyKey, jsonResponse, 24, TimeUnit.HOURS);
            } catch (Exception e) {
                // Ignore caching errors
            }
        }

        return response;
    }
}
