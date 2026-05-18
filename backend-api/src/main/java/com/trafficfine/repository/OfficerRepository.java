package com.trafficfine.repository;

import com.trafficfine.entity.Officer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficerRepository extends JpaRepository<Officer, Long> {
    Optional<Officer> findByBadgeNumber(String badgeNumber);
}
