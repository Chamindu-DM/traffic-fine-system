package com.trafficfine.reportingservice.repository;

import com.trafficfine.reportingservice.entity.TrafficFineReport;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrafficFineReportRepository extends JpaRepository<TrafficFineReport, Long>, JpaSpecificationExecutor<TrafficFineReport> {

    Optional<TrafficFineReport> findByReferenceNumber(String referenceNumber);

    long countByStatus(String status);

    @Query("select coalesce(sum(r.amount), 0) from TrafficFineReport r where r.status = 'PAID'")
    BigDecimal totalCollected();

    @Query("select r.district, coalesce(sum(r.amount), 0) from TrafficFineReport r where r.status = 'PAID' group by r.district order by r.district")
    List<Object[]> districtWiseCollections();

    @Query("select r.categoryName, coalesce(sum(r.amount), 0) from TrafficFineReport r where r.status = 'PAID' group by r.categoryName order by r.categoryName")
    List<Object[]> categoryWiseCollections();
}
