package com.trafficfine.repository;

import com.trafficfine.entity.FineCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FineCategoryRepository extends JpaRepository<FineCategory, Long> {
    Optional<FineCategory> findByCodeIgnoreCase(String code);
}
