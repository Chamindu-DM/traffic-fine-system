package com.trafficfine.controller;

import com.trafficfine.entity.FineCategory;
import com.trafficfine.repository.FineCategoryRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fine-categories")
public class FineCategoryController {

    private final FineCategoryRepository fineCategoryRepository;

    public FineCategoryController(FineCategoryRepository fineCategoryRepository) {
        this.fineCategoryRepository = fineCategoryRepository;
    }

    @GetMapping
    public List<FineCategory> getAll() {
        return fineCategoryRepository.findAll();
    }
}
