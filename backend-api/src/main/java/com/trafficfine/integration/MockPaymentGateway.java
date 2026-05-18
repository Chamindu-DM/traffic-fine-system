package com.trafficfine.integration;

import com.trafficfine.dto.PaymentRequest;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MockPaymentGateway {

    public PaymentGatewayResult charge(PaymentRequest request) {
        if (!StringUtils.hasText(request.paymentMethod())) {
            return PaymentGatewayResult.failed("Payment method is required");
        }

        String method = request.paymentMethod().trim().toUpperCase(Locale.ROOT);
        if ("CARD".equals(method) && !StringUtils.hasText(request.cardLastFourDigits())) {
            return PaymentGatewayResult.failed("Test card last four digits are required for card payments");
        }

        if ("0000".equals(request.cardLastFourDigits())) {
            return PaymentGatewayResult.failed("Mock payment gateway declined the test card");
        }

        return PaymentGatewayResult.success("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
    }

    public record PaymentGatewayResult(boolean successful, String paymentReference, String message) {
        static PaymentGatewayResult success(String paymentReference) {
            return new PaymentGatewayResult(true, paymentReference, "Payment approved by mock gateway");
        }

        static PaymentGatewayResult failed(String message) {
            return new PaymentGatewayResult(false, null, message);
        }
    }
}
