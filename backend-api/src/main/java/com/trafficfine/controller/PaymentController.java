package com.trafficfine.controller;

import com.trafficfine.dto.PaymentInitiateRequest;
import com.trafficfine.dto.PaymentRequest;
import com.trafficfine.dto.PaymentResponse;
import com.trafficfine.service.PayHereService;
import com.trafficfine.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
    public PaymentResponse pay(@Valid @RequestBody PaymentRequest request) {
        return paymentService.pay(request);
    }

    @PostMapping("/initiate")
    public Map<String, Object> initiate(@Valid @RequestBody PaymentInitiateRequest request) {
        return payHereService.createPaymentRequest(request.referenceNumber(), request.categoryCode());
    }

    @PostMapping(value = "/notify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void notify(@RequestParam Map<String, String> params) {
        payHereService.handleNotification(params);
    }
}
