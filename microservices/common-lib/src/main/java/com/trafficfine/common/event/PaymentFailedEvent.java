package com.trafficfine.common.event;

import java.io.Serializable;
import java.time.LocalDateTime;

public record PaymentFailedEvent(
        String fineReferenceNumber,
        String reason,
        LocalDateTime failedAt
) implements Serializable {}
