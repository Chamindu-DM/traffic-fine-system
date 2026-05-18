package com.trafficfine.service;

import com.trafficfine.dto.AdminDashboardResponse;
import com.trafficfine.dto.AdminFineResponse;
import com.trafficfine.dto.CollectionBreakdownResponse;
import com.trafficfine.entity.FineStatus;
import com.trafficfine.entity.TrafficFine;
import com.trafficfine.repository.TrafficFineRepository;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AdminReportService {

    private final TrafficFineRepository trafficFineRepository;
    private final FineMapper fineMapper;

    public AdminReportService(TrafficFineRepository trafficFineRepository, FineMapper fineMapper) {
        this.trafficFineRepository = trafficFineRepository;
        this.fineMapper = fineMapper;
    }

    public AdminDashboardResponse dashboard() {
        return new AdminDashboardResponse(
                trafficFineRepository.totalCollected(),
                trafficFineRepository.countByStatus(FineStatus.PAID),
                trafficFineRepository.countByStatus(FineStatus.UNPAID),
                trafficFineRepository.countByStatus(FineStatus.CANCELLED),
                toBreakdown(trafficFineRepository.districtWiseCollections()),
                toBreakdown(trafficFineRepository.categoryWiseCollections())
        );
    }

    public List<AdminFineResponse> searchFines(String district, String categoryCode, FineStatus status, LocalDate fromDate, LocalDate toDate) {
        Specification<TrafficFine> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(district)) {
                predicates.add(cb.equal(cb.lower(root.get("district")), district.trim().toLowerCase()));
            }
            if (StringUtils.hasText(categoryCode)) {
                predicates.add(cb.equal(cb.lower(root.get("category").get("code")), categoryCode.trim().toLowerCase()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("issuedAt"), fromDate.atStartOfDay()));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("issuedAt"), toDate.atTime(LocalTime.MAX)));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };

        return trafficFineRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "issuedAt"))
                .stream()
                .map(fineMapper::toAdminResponse)
                .toList();
    }

    private List<CollectionBreakdownResponse> toBreakdown(List<Object[]> rows) {
        return rows.stream()
                .map(row -> new CollectionBreakdownResponse((String) row[0], (BigDecimal) row[1]))
                .toList();
    }
}
