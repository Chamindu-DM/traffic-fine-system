package com.trafficfine.common.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentCompletedEvent(
        String paymentReference,
        String fineReferenceNumber,
        BigDecimal amount,
        String officerPhoneNumber,
        String officerName,
        LocalDateTime paidAt
) implements Serializable {}
