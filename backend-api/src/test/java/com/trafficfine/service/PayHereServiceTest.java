package com.trafficfine.service;

import com.trafficfine.entity.*;
import com.trafficfine.repository.PaymentRepository;
import com.trafficfine.repository.TrafficFineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PayHereServiceTest {

    private FineService fineService;
    private TrafficFineRepository trafficFineRepository;
    private PaymentRepository paymentRepository;
    private SmsService smsService;
    private PayHereService payHereService;

    @BeforeEach
    void setUp() {
        fineService = mock(FineService.class);
        trafficFineRepository = mock(TrafficFineRepository.class);
        paymentRepository = mock(PaymentRepository.class);
        smsService = mock(SmsService.class);

        payHereService = new PayHereService(
                fineService,
                trafficFineRepository,
                paymentRepository,
                smsService
        );

        // Inject configuration values using ReflectionTestUtils
        ReflectionTestUtils.setField(payHereService, "merchantId", "M12345");
        ReflectionTestUtils.setField(payHereService, "secret", "secretKey123");
        ReflectionTestUtils.setField(payHereService, "sandbox", true);
        ReflectionTestUtils.setField(payHereService, "notifyUrl", "http://localhost:8080/api/payments/notify");
        ReflectionTestUtils.setField(payHereService, "returnUrl", "http://localhost:5173/payment-success");
        ReflectionTestUtils.setField(payHereService, "cancelUrl", "http://localhost:5173/payment-cancel");
    }

    @Test
    void testGenerateHash() {
        String hash = payHereService.generateHash("TF123456", new BigDecimal("5000.00"), "LKR");
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
        assertEquals("322D9C5B859CBEF8CD9D71BA7935512F", hash);
    }

    @Test
    void testCreatePaymentRequest_Success() {
        FineCategory category = new FineCategory("SPEEDING", "Speeding", "Driving fast", new BigDecimal("5000.00"));
        Officer officer = new Officer("J. Doe", "MT-100", "0771112222", "Matara");
        TrafficFine fine = new TrafficFine("TF123456", category, officer, "DL123", "V123", "Matara", FineStatus.UNPAID, null);

        when(fineService.findFine("TF123456", "SPEEDING")).thenReturn(fine);

        Map<String, Object> request = payHereService.createPaymentRequest("TF123456", "SPEEDING");

        assertNotNull(request);
        assertEquals(true, request.get("sandbox"));
        assertEquals("M12345", request.get("merchant_id"));
        assertEquals("http://localhost:5173/payment-success", request.get("return_url"));
        assertEquals("http://localhost:5173/payment-cancel", request.get("cancel_url"));
        assertEquals("http://localhost:8080/api/payments/notify", request.get("notify_url"));
        assertEquals("TF123456", request.get("order_id"));
        assertEquals("Traffic Fine - SPEEDING", request.get("items"));
        assertEquals("5000.00", request.get("amount"));
        assertEquals("LKR", request.get("currency"));
        assertEquals("Driver", request.get("first_name"));
        assertEquals("Name", request.get("last_name"));
        assertEquals("driver@example.com", request.get("email"));
        assertEquals("0771234567", request.get("phone"));
        assertEquals("Sri Lanka", request.get("address"));
        assertEquals("Colombo", request.get("city"));
        assertEquals("Sri Lanka", request.get("country"));
        assertNotNull(request.get("hash"));
    }

    @Test
    void testCreatePaymentRequest_AlreadyPaid() {
        FineCategory category = new FineCategory("SPEEDING", "Speeding", "Driving fast", new BigDecimal("5000.00"));
        Officer officer = new Officer("J. Doe", "MT-100", "0771112222", "Matara");
        TrafficFine fine = new TrafficFine("TF123456", category, officer, "DL123", "V123", "Matara", FineStatus.PAID, null);

        when(fineService.findFine("TF123456", "SPEEDING")).thenReturn(fine);

        Exception exception = assertThrows(BusinessRuleException.class, () -> {
            payHereService.createPaymentRequest("TF123456", "SPEEDING");
        });

        assertEquals("This fine has already been paid", exception.getMessage());
    }

    @Test
    void testCreatePaymentRequest_Cancelled() {
        FineCategory category = new FineCategory("SPEEDING", "Speeding", "Driving fast", new BigDecimal("5000.00"));
        Officer officer = new Officer("J. Doe", "MT-100", "0771112222", "Matara");
        TrafficFine fine = new TrafficFine("TF123456", category, officer, "DL123", "V123", "Matara", FineStatus.CANCELLED, null);

        when(fineService.findFine("TF123456", "SPEEDING")).thenReturn(fine);

        Exception exception = assertThrows(BusinessRuleException.class, () -> {
            payHereService.createPaymentRequest("TF123456", "SPEEDING");
        });

        assertEquals("Cancelled fines cannot be paid", exception.getMessage());
    }

    @Test
    void testHandleNotification_Success() {
        FineCategory category = new FineCategory("SPEEDING", "Speeding", "Driving fast", new BigDecimal("5000.00"));
        Officer officer = new Officer("J. Doe", "MT-100", "0771112222", "Matara");
        TrafficFine fine = new TrafficFine("TF123456", category, officer, "DL123", "V123", "Matara", FineStatus.UNPAID, null);

        when(trafficFineRepository.findByReferenceNumber("TF123456")).thenReturn(Optional.of(fine));

        // Let's create params for notify
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "M12345");
        params.put("order_id", "TF123456");
        params.put("payhere_amount", "5000.00");
        params.put("payhere_currency", "LKR");
        params.put("status_code", "2");
        params.put("payment_id", "P-100");
        params.put("method", "VISA");

        // generate signature
        params.put("md5sig", "4C64A75E15FB504DFF9CD098FF30E0E0");

        payHereService.handleNotification(params);

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(trafficFineRepository, times(1)).save(fine);
        verify(smsService, times(1)).sendPaymentConfirmation(fine);

        assertEquals(FineStatus.PAID, fine.getStatus());
        assertNotNull(fine.getPaidAt());
    }

    @Test
    void testHandleNotification_InvalidSig() {
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "M12345");
        params.put("order_id", "TF123456");
        params.put("payhere_amount", "5000.00");
        params.put("payhere_currency", "LKR");
        params.put("status_code", "2");
        params.put("payment_id", "P-100");
        params.put("md5sig", "WRONG_SIGNATURE");

        Exception exception = assertThrows(BusinessRuleException.class, () -> {
            payHereService.handleNotification(params);
        });

        assertEquals("Invalid MD5 signature", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(trafficFineRepository, never()).save(any(TrafficFine.class));
        verify(smsService, never()).sendPaymentConfirmation(any(TrafficFine.class));
    }

    @Test
    void testHandleNotification_StatusCodeNot2() {
        // Let's create params for notify where status_code is not 2
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "M12345");
        params.put("order_id", "TF123456");
        params.put("payhere_amount", "5000.00");
        params.put("payhere_currency", "LKR");
        params.put("status_code", "0"); // e.g. pending
        params.put("payment_id", "P-100");

        params.put("md5sig", "53E1F43E5B311045DEC054ED2CEAAE9B");

        payHereService.handleNotification(params);

        // Should not save payment or mark fine as paid because status_code is not 2
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(trafficFineRepository, never()).save(any(TrafficFine.class));
        verify(smsService, never()).sendPaymentConfirmation(any(TrafficFine.class));
    }
}
