package com.trafficfine.paymentservice.controller;

import com.trafficfine.common.dto.PaymentRequest;
import com.trafficfine.common.dto.PaymentResponse;
import com.trafficfine.paymentservice.dto.PaymentInitiateRequest;
import com.trafficfine.paymentservice.service.PayHereService;
import com.trafficfine.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PayHereService payHereService;

    public PaymentController(PaymentService paymentService, PayHereService payHereService) {
        this.paymentService = paymentService;
        this.payHereService = payHereService;
    }

    @PostMapping
    public PaymentResponse pay(
            @Valid @RequestBody PaymentRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) {
        return paymentService.pay(request, idempotencyKey);
    }

    @PostMapping("/initiate")
    public Map<String, Object> initiate(@Valid @RequestBody PaymentInitiateRequest request) {
        return payHereService.createPaymentRequest(request.referenceNumber());
    }

    @PostMapping(value = "/notify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void notify(@RequestParam Map<String, String> params) {
        payHereService.handleNotification(params);
    }

    @PostMapping("/simulate-webhook")
    public void simulateWebhook(@RequestParam("referenceNumber") String referenceNumber) {
        payHereService.simulateWebhook(referenceNumber);
    }
}
