package com.trafficfine.reportingservice.controller;

import com.trafficfine.common.dto.AdminDashboardResponse;
import com.trafficfine.reportingservice.dto.AdminFineResponse;
import com.trafficfine.reportingservice.service.AdminReportService;
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

    public AdminController(AdminReportService adminReportService) {
        this.adminReportService = adminReportService;
    }

    @GetMapping("/dashboard")
    public AdminDashboardResponse dashboard() {
        return adminReportService.dashboard();
    }

    @GetMapping("/fines")
    public List<AdminFineResponse> fines(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return adminReportService.searchFines(district, categoryCode, status, fromDate, toDate);
    }
}
