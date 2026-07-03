package com.trafficfine.notificationservice.listener;

import com.trafficfine.common.event.PaymentCompletedEvent;
import com.trafficfine.common.event.RabbitMQConstants;
import com.trafficfine.notificationservice.entity.SmsLog;
import com.trafficfine.notificationservice.entity.SmsStatus;
import com.trafficfine.notificationservice.repository.SmsLogRepository;
import com.trafficfine.notificationservice.sms.SmsProvider;
import com.trafficfine.notificationservice.sms.SmsSendResult;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);

    private final SmsProvider smsProvider;
    private final SmsProvider mockSmsProvider;
    private final SmsLogRepository smsLogRepository;

    public PaymentEventListener(
            @Qualifier("smsProvider") SmsProvider smsProvider,
            @Qualifier("mockSmsProvider") SmsProvider mockSmsProvider,
            SmsLogRepository smsLogRepository
    ) {
        this.smsProvider = smsProvider;
        this.mockSmsProvider = mockSmsProvider;
        this.smsLogRepository = smsLogRepository;
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_PAYMENT_COMPLETED_NOTIFICATION)
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        log.info("Received PaymentCompletedEvent for fine: {}", event.fineReferenceNumber());

        String phoneNumber = event.officerPhoneNumber();
        String officerName = event.officerName();
        String message = "Payment confirmed for fine " + event.fineReferenceNumber()
                + ". Driver license can be released.";

        SmsSendResult result = sendWithFallback(phoneNumber, message);
        SmsStatus status = result.successful() ? SmsStatus.SUCCESS : SmsStatus.FAILED;

        SmsLog smsLog = new SmsLog(
                event.fineReferenceNumber(),
                officerName,
                phoneNumber,
                message,
                status,
                LocalDateTime.now()
        );
        smsLogRepository.save(smsLog);
    }

    private SmsSendResult sendWithFallback(String phoneNumber, String message) {
        try {
            SmsSendResult result = smsProvider.sendSms(phoneNumber, message);
            if (result.successful()) {
                return result;
            }

            log.warn("Primary SMS provider failed: {}. Falling back to mock SMS.", result.errorMessage());
            return mockSmsProvider.sendSms(phoneNumber, message);
        } catch (Exception e) {
            log.warn("Primary SMS provider threw exception: {}. Falling back to mock SMS.", e.getMessage());
            return mockSmsProvider.sendSms(phoneNumber, message);
        }
    }
}
