package com.trafficfine.fineservice.repository;

import com.trafficfine.fineservice.entity.TrafficFine;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrafficFineRepository extends JpaRepository<TrafficFine, Long> {

    @EntityGraph(attributePaths = {"category", "officer"})
    Optional<TrafficFine> findByReferenceNumberAndCategoryCodeIgnoreCase(String referenceNumber, String categoryCode);

    @EntityGraph(attributePaths = {"category", "officer"})
    Optional<TrafficFine> findByReferenceNumber(String referenceNumber);

    boolean existsByReferenceNumber(String referenceNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from TrafficFine f where f.referenceNumber = :referenceNumber")
    Optional<TrafficFine> findByReferenceNumberForUpdate(String referenceNumber);
}
