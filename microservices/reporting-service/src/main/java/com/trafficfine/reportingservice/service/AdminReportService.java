package com.trafficfine.reportingservice.service;

import com.trafficfine.common.dto.AdminDashboardResponse;
import com.trafficfine.common.dto.CollectionBreakdownResponse;
import com.trafficfine.reportingservice.dto.AdminFineResponse;
import com.trafficfine.reportingservice.entity.TrafficFineReport;
import com.trafficfine.reportingservice.repository.TrafficFineReportRepository;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class AdminReportService {

    private final TrafficFineReportRepository repository;

    public AdminReportService(TrafficFineReportRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "dashboard", key = "'stats'")
    public AdminDashboardResponse dashboard() {
        return new AdminDashboardResponse(
                repository.totalCollected(),
                repository.countByStatus("PAID"),
                repository.countByStatus("UNPAID"),
                repository.countByStatus("CANCELLED"),
                toBreakdown(repository.districtWiseCollections()),
                toBreakdown(repository.categoryWiseCollections())
        );
    }

    public List<AdminFineResponse> searchFines(String district, String categoryCode, String status, LocalDate fromDate, LocalDate toDate) {
        Specification<TrafficFineReport> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(district)) {
                predicates.add(cb.equal(cb.lower(root.get("district")), district.trim().toLowerCase()));
            }
            if (StringUtils.hasText(categoryCode)) {
                predicates.add(cb.equal(cb.lower(root.get("categoryCode")), categoryCode.trim().toLowerCase()));
            }
            if (StringUtils.hasText(status) && !"ALL".equalsIgnoreCase(status)) {
                predicates.add(cb.equal(cb.upper(root.get("status")), status.trim().toUpperCase()));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("issuedAt"), fromDate.atStartOfDay()));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("issuedAt"), toDate.atTime(LocalTime.MAX)));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };

        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "issuedAt"))
                .stream()
                .map(r -> new AdminFineResponse(
                        r.getReferenceNumber(),
                        r.getCategoryName(),
                        r.getDistrict(),
                        r.getAmount(),
                        r.getStatus(),
                        r.getIssuedAt()
                ))
                .toList();
    }

    private List<CollectionBreakdownResponse> toBreakdown(List<Object[]> rows) {
        return rows.stream()
                .map(row -> new CollectionBreakdownResponse((String) row[0], (BigDecimal) row[1]))
                .toList();
    }
}
