package com.trafficfine.fineservice.repository;

import com.trafficfine.fineservice.entity.Officer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficerRepository extends JpaRepository<Officer, Long> {
    Optional<Officer> findByBadgeNumber(String badgeNumber);
}
