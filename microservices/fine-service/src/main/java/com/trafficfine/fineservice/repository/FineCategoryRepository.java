package com.trafficfine.fineservice.repository;

import com.trafficfine.fineservice.entity.FineCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FineCategoryRepository extends JpaRepository<FineCategory, Long> {
    Optional<FineCategory> findByCodeIgnoreCase(String code);
}
