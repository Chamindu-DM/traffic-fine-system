package com.trafficfine.repository;

import com.trafficfine.entity.FineStatus;
import com.trafficfine.entity.TrafficFine;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface TrafficFineRepository extends JpaRepository<TrafficFine, Long>, JpaSpecificationExecutor<TrafficFine> {
    Optional<TrafficFine> findByReferenceNumberAndCategoryCodeIgnoreCase(String referenceNumber, String categoryCode);

    long countByStatus(FineStatus status);

    @Query("select coalesce(sum(f.amount), 0) from TrafficFine f where f.status = com.trafficfine.entity.FineStatus.PAID")
    BigDecimal totalCollected();

    @Query("""
            select f.district, coalesce(sum(f.amount), 0)
            from TrafficFine f
            where f.status = com.trafficfine.entity.FineStatus.PAID
            group by f.district
            order by f.district
            """)
    List<Object[]> districtWiseCollections();

    @Query("""
            select f.category.name, coalesce(sum(f.amount), 0)
            from TrafficFine f
            where f.status = com.trafficfine.entity.FineStatus.PAID
            group by f.category.name
            order by f.category.name
            """)
    List<Object[]> categoryWiseCollections();
}
