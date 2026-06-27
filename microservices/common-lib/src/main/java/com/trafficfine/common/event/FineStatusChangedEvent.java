package com.trafficfine.common.event;

import java.io.Serializable;
import java.time.LocalDateTime;

public record FineStatusChangedEvent(
        String referenceNumber,
        String oldStatus,
        String newStatus,
        LocalDateTime changedAt
) implements Serializable {}
