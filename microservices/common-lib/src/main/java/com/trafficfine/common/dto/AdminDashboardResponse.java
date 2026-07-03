package com.trafficfine.common.dto;

import java.math.BigDecimal;
import java.util.List;

public record AdminDashboardResponse(
        BigDecimal totalCollected,
        long paidFineCount,
        long unpaidFineCount,
        long cancelledFineCount,
        List<CollectionBreakdownResponse> districtWiseCollections,
        List<CollectionBreakdownResponse> categoryWiseCollections
) {}
