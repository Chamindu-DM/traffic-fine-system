package com.trafficfine.common.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FineCreatedEvent(
        String referenceNumber,
        String categoryCode,
        BigDecimal amount,
        String district,
        String officerName,
        LocalDateTime issuedAt
) implements Serializable {}
