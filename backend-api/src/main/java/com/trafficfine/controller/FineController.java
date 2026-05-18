package com.trafficfine.controller;

import com.trafficfine.dto.FineLookupResponse;
import com.trafficfine.service.FineService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/fines")
public class FineController {

    private final FineService fineService;

    public FineController(FineService fineService) {
        this.fineService = fineService;
    }

    @GetMapping("/lookup")
    public FineLookupResponse lookup(
            @RequestParam @NotBlank String referenceNumber,
            @RequestParam @NotBlank String categoryCode
    ) {
        return fineService.lookup(referenceNumber, categoryCode);
    }
}
