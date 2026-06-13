package com.trafficfine.service;

import com.trafficfine.entity.FineStatus;
import com.trafficfine.entity.Payment;
import com.trafficfine.entity.PaymentStatus;
import com.trafficfine.entity.TrafficFine;
import com.trafficfine.repository.PaymentRepository;
import com.trafficfine.repository.TrafficFineRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class PayHereService {

    private final FineService fineService;
    private final TrafficFineRepository trafficFineRepository;
    private final PaymentRepository paymentRepository;
    private final SmsService smsService;

    @Value("${payhere.merchant-id}")
    private String merchantId;

    @Value("${payhere.secret}")
    private String secret;

    @Value("${payhere.sandbox}")
    private boolean sandbox;

    @Value("${payhere.notify-url}")
    private String notifyUrl;

    @Value("${payhere.return-url}")
    private String returnUrl;

    @Value("${payhere.cancel-url}")
    private String cancelUrl;

    public PayHereService(
            FineService fineService,
            TrafficFineRepository trafficFineRepository,
            PaymentRepository paymentRepository,
            SmsService smsService) {
        this.fineService = fineService;
        this.trafficFineRepository = trafficFineRepository;
        this.paymentRepository = paymentRepository;
        this.smsService = smsService;
    }

    public String generateHash(String orderId, BigDecimal amount, String currency) {
        String formattedAmount = String.format(Locale.US, "%.2f", amount);
        String secretMd5 = md5Hex(secret).toUpperCase();
        String raw = merchantId + orderId + formattedAmount + currency + secretMd5;
        return md5Hex(raw).toUpperCase();
    }

    public Map<String, Object> createPaymentRequest(String referenceNumber, String categoryCode) {
        TrafficFine fine = fineService.findFine(referenceNumber, categoryCode);

        if (fine.getStatus() == FineStatus.PAID) {
            throw new BusinessRuleException("This fine has already been paid");
        }
        if (fine.getStatus() == FineStatus.CANCELLED) {
            throw new BusinessRuleException("Cancelled fines cannot be paid");
        }

        BigDecimal amount = fine.getAmount();
        String formattedAmount = String.format(Locale.US, "%.2f", amount);
        String orderId = fine.getReferenceNumber();
        String currency = "LKR";

        String hash = generateHash(orderId, amount, currency);

        Map<String, Object> request = new HashMap<>();
        request.put("sandbox", sandbox);
        request.put("merchant_id", merchantId);
        request.put("return_url", returnUrl);
        request.put("cancel_url", cancelUrl);
        request.put("notify_url", notifyUrl);
        request.put("order_id", orderId);
        request.put("items", "Traffic Fine - " + fine.getCategory().getCode());
        request.put("amount", formattedAmount);
        request.put("currency", currency);
        request.put("hash", hash);
        request.put("first_name", "Driver");
        request.put("last_name", "Name");
        request.put("email", "driver@example.com");
        request.put("phone", "0771234567");
        request.put("address", "Sri Lanka");
        request.put("city", "Colombo");
        request.put("country", "Sri Lanka");

        return request;
    }

    @Transactional
    public void handleNotification(Map<String, String> params) {
        if (!verifyNotification(params)) {
            throw new BusinessRuleException("Invalid MD5 signature");
        }

        String statusCode = params.get("status_code");
        if ("2".equals(statusCode)) {
            String orderId = params.get("order_id");
            String paymentId = params.get("payment_id");
            String payhereAmount = params.get("payhere_amount");
            String method = params.getOrDefault("method", "PAYHERE");

            TrafficFine fine = trafficFineRepository.findByReferenceNumber(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Fine not found: " + orderId));

            if (fine.getStatus() == FineStatus.PAID) {
                // Already processed, ignore to prevent duplicate logs/SMS
                return;
            }

            LocalDateTime paidAt = LocalDateTime.now();
            BigDecimal amount = new BigDecimal(payhereAmount);

            Payment payment = new Payment(
                    fine,
                    paymentId,
                    amount,
                    method.toUpperCase(),
                    PaymentStatus.SUCCESS,
                    paidAt
            );
            paymentRepository.save(payment);

            fine.markPaid(paidAt);
            trafficFineRepository.save(fine);

            smsService.sendPaymentConfirmation(fine);
        }
    }

    private boolean verifyNotification(Map<String, String> params) {
        String merchantIdParam = params.get("merchant_id");
        String orderIdParam = params.get("order_id");
        String payhereAmountParam = params.get("payhere_amount");
        String payhereCurrencyParam = params.get("payhere_currency");
        String statusCodeParam = params.get("status_code");
        String md5sigParam = params.get("md5sig");

        if (merchantIdParam == null || orderIdParam == null || payhereAmountParam == null ||
                payhereCurrencyParam == null || statusCodeParam == null || md5sigParam == null) {
            return false;
        }

        String secretMd5 = md5Hex(secret).toUpperCase();
        String raw = merchantIdParam + orderIdParam + payhereAmountParam + payhereCurrencyParam + statusCodeParam + secretMd5;
        String calculatedSig = md5Hex(raw).toUpperCase();

        return calculatedSig.equalsIgnoreCase(md5sigParam);
    }

    private String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 hashing failed", e);
        }
    }
}
