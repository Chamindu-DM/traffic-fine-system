package com.trafficfine.fineservice.controller;

import com.trafficfine.common.dto.CreateFineRequest;
import com.trafficfine.common.dto.CreateFineResponse;
import com.trafficfine.common.dto.FineLookupResponse;
import com.trafficfine.fineservice.service.FineService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fines")
public class FineController {

    private final FineService fineService;

    public FineController(FineService fineService) {
        this.fineService = fineService;
    }

    @PostMapping
    public CreateFineResponse createFine(@Valid @RequestBody CreateFineRequest request) {
        return fineService.createFine(request);
    }

    @GetMapping("/lookup")
    public FineLookupResponse lookupFine(
            @RequestParam String referenceNumber,
            @RequestParam String categoryCode
    ) {
        return fineService.lookupFine(referenceNumber, categoryCode);
    }

    @GetMapping("/{referenceNumber}")
    public FineLookupResponse getFine(@PathVariable String referenceNumber) {
        return fineService.getFine(referenceNumber);
    }

    @PutMapping("/{referenceNumber}/status")
    public void updateFineStatus(
            @PathVariable String referenceNumber,
            @RequestParam String status
    ) {
        fineService.updateFineStatus(referenceNumber, status);
    }
}
