package com.trafficfine.paymentservice.client;

import com.trafficfine.common.dto.FineLookupResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "fine-service")
public interface FineClient {

    @GetMapping("/api/fines/{referenceNumber}")
    FineLookupResponse getFine(@PathVariable("referenceNumber") String referenceNumber);

    @PutMapping("/api/fines/{referenceNumber}/status")
    void updateFineStatus(
            @PathVariable("referenceNumber") String referenceNumber,
            @RequestParam("status") String status
    );
}
