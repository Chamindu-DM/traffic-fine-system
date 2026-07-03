package com.trafficfine.controller;

import com.trafficfine.dto.AdminDashboardResponse;
import com.trafficfine.dto.AdminFineResponse;
import com.trafficfine.dto.FineCategoryDto;
import com.trafficfine.dto.OfficerDto;
import com.trafficfine.entity.FineStatus;
import com.trafficfine.repository.FineCategoryRepository;
import com.trafficfine.repository.OfficerRepository;
import com.trafficfine.service.AdminReportService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminReportService adminReportService;
    private final FineCategoryRepository fineCategoryRepository;
    private final OfficerRepository officerRepository;

    public AdminController(
            AdminReportService adminReportService,
            FineCategoryRepository fineCategoryRepository,
            OfficerRepository officerRepository
    ) {
        this.adminReportService = adminReportService;
        this.fineCategoryRepository = fineCategoryRepository;
        this.officerRepository = officerRepository;
    }

    @GetMapping("/dashboard")
    public AdminDashboardResponse dashboard() {
        return adminReportService.dashboard();
    }

    @GetMapping("/fines")
    public List<AdminFineResponse> fines(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) FineStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return adminReportService.searchFines(district, categoryCode, status, fromDate, toDate);
    }

    @GetMapping("/categories")
    public List<FineCategoryDto> categories() {
        return fineCategoryRepository.findAll().stream()
                .map(c -> new FineCategoryDto(c.getId(), c.getCode(), c.getName(), c.getAmount()))
                .toList();
    }

    @GetMapping("/officers")
    public List<OfficerDto> officers() {
        return officerRepository.findAll().stream()
                .map(o -> new OfficerDto(o.getId(), o.getName(), o.getBadgeNumber(), o.getDistrict()))
                .toList();
    }
}
