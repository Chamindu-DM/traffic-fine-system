package com.trafficfine.fineservice.controller;

import com.trafficfine.fineservice.entity.FineCategory;
import com.trafficfine.fineservice.service.FineService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fine-categories")
public class FineCategoryController {

    private final FineService fineService;

    public FineCategoryController(FineService fineService) {
        this.fineService = fineService;
    }

    @GetMapping
    public List<FineCategory> getCategories() {
        return fineService.getAllCategories();
    }
}
