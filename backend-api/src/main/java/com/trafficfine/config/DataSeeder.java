package com.trafficfine.config;

import com.trafficfine.entity.FineCategory;
import com.trafficfine.entity.FineStatus;
import com.trafficfine.entity.Officer;
import com.trafficfine.entity.Role;
import com.trafficfine.entity.TrafficFine;
import com.trafficfine.entity.User;
import com.trafficfine.repository.FineCategoryRepository;
import com.trafficfine.repository.OfficerRepository;
import com.trafficfine.repository.TrafficFineRepository;
import com.trafficfine.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            UserRepository userRepository,
            OfficerRepository officerRepository,
            FineCategoryRepository fineCategoryRepository,
            TrafficFineRepository trafficFineRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                userRepository.save(new User("Senior Police Admin", "admin", passwordEncoder.encode("password"), Role.ADMIN));
            }

            FineCategory speeding = fineCategoryRepository.findByCodeIgnoreCase("SPEEDING")
                    .orElseGet(() -> fineCategoryRepository.save(new FineCategory(
                            "SPEEDING",
                            "Speeding",
                            "Driving above the allowed speed limit",
                            new BigDecimal("5000.00")
                    )));
            FineCategory signal = fineCategoryRepository.findByCodeIgnoreCase("SIGNAL")
                    .orElseGet(() -> fineCategoryRepository.save(new FineCategory(
                            "SIGNAL",
                            "Traffic Signal Violation",
                            "Failing to obey a traffic signal",
                            new BigDecimal("3000.00")
                    )));
            FineCategory parking = fineCategoryRepository.findByCodeIgnoreCase("PARKING")
                    .orElseGet(() -> fineCategoryRepository.save(new FineCategory(
                            "PARKING",
                            "Illegal Parking",
                            "Parking in a restricted zone",
                            new BigDecimal("2000.00")
                    )));

            Officer mataraOfficer = officerRepository.findByBadgeNumber("MT-1024")
                    .orElseGet(() -> officerRepository.save(new Officer("S. Perera", "MT-1024", "+94711234567", "Matara")));
            Officer colomboOfficer = officerRepository.findByBadgeNumber("CO-2048")
                    .orElseGet(() -> officerRepository.save(new Officer("N. Silva", "CO-2048", "+94719876543", "Colombo")));

            if (trafficFineRepository.count() == 0) {
                TrafficFine unpaid = new TrafficFine(
                        "TF123456",
                        speeding,
                        mataraOfficer,
                        "B1234567",
                        "SP-1234",
                        "Matara",
                        FineStatus.UNPAID,
                        LocalDateTime.now().minusDays(2)
                );
                TrafficFine paid = new TrafficFine(
                        "TF654321",
                        signal,
                        colomboOfficer,
                        "B7654321",
                        "WP-4567",
                        "Colombo",
                        FineStatus.UNPAID,
                        LocalDateTime.now().minusDays(6)
                );
                paid.markPaid(LocalDateTime.now().minusDays(5));
                TrafficFine cancelled = new TrafficFine(
                        "TF777888",
                        parking,
                        mataraOfficer,
                        "B9988776",
                        "SP-8877",
                        "Matara",
                        FineStatus.CANCELLED,
                        LocalDateTime.now().minusDays(4)
                );

                trafficFineRepository.save(unpaid);
                trafficFineRepository.save(paid);
                trafficFineRepository.save(cancelled);
            }
        };
    }
}
