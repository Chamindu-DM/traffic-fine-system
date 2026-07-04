package com.trafficfine.fineservice.config;

import com.trafficfine.fineservice.entity.FineCategory;
import com.trafficfine.fineservice.entity.FineStatus;
import com.trafficfine.fineservice.entity.Officer;
import com.trafficfine.fineservice.entity.TrafficFine;
import com.trafficfine.fineservice.repository.FineCategoryRepository;
import com.trafficfine.fineservice.repository.OfficerRepository;
import com.trafficfine.fineservice.repository.TrafficFineRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedFineData(
            FineCategoryRepository fineCategoryRepository,
            OfficerRepository officerRepository,
            TrafficFineRepository trafficFineRepository,
            RabbitTemplate rabbitTemplate
    ) {
        return args -> {
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
        FineCategory drunkDriving = fineCategoryRepository.findByCodeIgnoreCase("DRUNK_DRIVING")
                    .orElseGet(() -> fineCategoryRepository.save(new FineCategory(
                            "DRUNK_DRIVING",
                            "Drunk Driving",
                            "Operating vehicle under alcohol influence",
                            new BigDecimal("25000.00")
                    )));
            FineCategory noHelmet = fineCategoryRepository.findByCodeIgnoreCase("NO_HELMET")
                    .orElseGet(() -> fineCategoryRepository.save(new FineCategory(
                            "NO_HELMET",
                            "No Helmet",
                            "Riding without a helmet",
                            new BigDecimal("1000.00")
                    )));
            FineCategory overloading = fineCategoryRepository.findByCodeIgnoreCase("OVERLOADING")
                    .orElseGet(() -> fineCategoryRepository.save(new FineCategory(
                            "OVERLOADING",
                            "Overloading",
                            "Exceeding vehicle load capacity",
                            new BigDecimal("8000.00")
                    )));

            Officer mataraOfficer = officerRepository.findByBadgeNumber("MT-1024")
                    .orElseGet(() -> officerRepository.save(new Officer("S. Perera", "MT-1024", "+94711234567", "Matara")));
            Officer colomboOfficer = officerRepository.findByBadgeNumber("CO-2048")
                    .orElseGet(() -> officerRepository.save(new Officer("N. Silva", "CO-2048", "+94719876543", "Colombo")));
            Officer kandyOfficer = officerRepository.findByBadgeNumber("KN-3001")
                    .orElseGet(() -> officerRepository.save(new Officer("K. Fernando", "KN-3001", "+94751234567", "Kandy")));
            Officer galleOfficer = officerRepository.findByBadgeNumber("GL-4002")
                    .orElseGet(() -> officerRepository.save(new Officer("R. Jayasinghe", "GL-4002", "+94761234567", "Galle")));
            Officer jaffnaOfficer = officerRepository.findByBadgeNumber("JA-5003")
                    .orElseGet(() -> officerRepository.save(new Officer("M. Thiruchelvam", "JA-5003", "+94771234567", "Jaffna")));

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

                saveAndPublish(unpaid, trafficFineRepository, rabbitTemplate);
                saveAndPublish(paid, trafficFineRepository, rabbitTemplate);
                saveAndPublish(cancelled, trafficFineRepository, rabbitTemplate);

                // Add 12 more TrafficFine records
                TrafficFine f1 = new TrafficFine("TF100001", speeding, colomboOfficer, "B1000001", "WP-1001", "Colombo", FineStatus.UNPAID, LocalDateTime.now().minusDays(25));
                saveAndPublish(f1, trafficFineRepository, rabbitTemplate);

                TrafficFine f2 = new TrafficFine("TF100002", signal, colomboOfficer, "B1000002", "WP-1002", "Colombo", FineStatus.UNPAID, LocalDateTime.now().minusDays(20));
                f2.markPaid(LocalDateTime.now().minusDays(18));
                saveAndPublish(f2, trafficFineRepository, rabbitTemplate);

                TrafficFine f3 = new TrafficFine("TF100003", parking, mataraOfficer, "B1000003", "SP-1003", "Matara", FineStatus.UNPAID, LocalDateTime.now().minusDays(15));
                saveAndPublish(f3, trafficFineRepository, rabbitTemplate);

                TrafficFine f4 = new TrafficFine("TF100004", drunkDriving, mataraOfficer, "B1000004", "SP-1004", "Matara", FineStatus.UNPAID, LocalDateTime.now().minusDays(14));
                f4.markPaid(LocalDateTime.now().minusDays(12));
                saveAndPublish(f4, trafficFineRepository, rabbitTemplate);

                TrafficFine f5 = new TrafficFine("TF100005", noHelmet, kandyOfficer, "B1000005", "CP-1005", "Kandy", FineStatus.UNPAID, LocalDateTime.now().minusDays(8));
                saveAndPublish(f5, trafficFineRepository, rabbitTemplate);

                TrafficFine f6 = new TrafficFine("TF100006", overloading, kandyOfficer, "B1000006", "CP-1006", "Kandy", FineStatus.UNPAID, LocalDateTime.now().minusDays(5));
                f6.markPaid(LocalDateTime.now().minusDays(2));
                saveAndPublish(f6, trafficFineRepository, rabbitTemplate);

                TrafficFine f7 = new TrafficFine("TF100007", speeding, galleOfficer, "B1000007", "SP-1007", "Galle", FineStatus.UNPAID, LocalDateTime.now().minusDays(30));
                saveAndPublish(f7, trafficFineRepository, rabbitTemplate);

                TrafficFine f8 = new TrafficFine("TF100008", signal, galleOfficer, "B1000008", "SP-1008", "Galle", FineStatus.UNPAID, LocalDateTime.now().minusDays(28));
                f8.markPaid(LocalDateTime.now().minusDays(24));
                saveAndPublish(f8, trafficFineRepository, rabbitTemplate);

                TrafficFine f9 = new TrafficFine("TF100009", parking, jaffnaOfficer, "B1000009", "NP-1009", "Jaffna", FineStatus.UNPAID, LocalDateTime.now().minusDays(3));
                saveAndPublish(f9, trafficFineRepository, rabbitTemplate);

                TrafficFine f10 = new TrafficFine("TF100010", drunkDriving, jaffnaOfficer, "B1000010", "NP-1010", "Jaffna", FineStatus.UNPAID, LocalDateTime.now().minusDays(2));
                f10.markPaid(LocalDateTime.now().minusDays(1));
                saveAndPublish(f10, trafficFineRepository, rabbitTemplate);

                TrafficFine f11 = new TrafficFine("TF100011", noHelmet, colomboOfficer, "B1000011", "WP-1011", "Colombo", FineStatus.UNPAID, LocalDateTime.now().minusDays(1));
                saveAndPublish(f11, trafficFineRepository, rabbitTemplate);

                TrafficFine f12 = new TrafficFine("TF100012", overloading, galleOfficer, "B1000012", "SP-1012", "Galle", FineStatus.UNPAID, LocalDateTime.now());
                f12.markPaid(LocalDateTime.now());
                saveAndPublish(f12, trafficFineRepository, rabbitTemplate);
        };
    }

    private void saveAndPublish(TrafficFine fine, TrafficFineRepository repo, RabbitTemplate rabbit) {
        if (!repo.existsByReferenceNumber(fine.getReferenceNumber())) {
            repo.save(fine);
        }

        // Publish Created Event
        com.trafficfine.common.event.FineCreatedEvent createdEvent = new com.trafficfine.common.event.FineCreatedEvent(
                fine.getReferenceNumber(),
                fine.getCategory().getCode(),
                fine.getAmount(),
                fine.getDistrict(),
                fine.getOfficer().getName(),
                fine.getIssuedAt()
        );
        rabbit.convertAndSend(com.trafficfine.common.event.RabbitMQConstants.EXCHANGE, com.trafficfine.common.event.RabbitMQConstants.ROUTING_KEY_FINE_CREATED, createdEvent);

        // If status is not UNPAID, publish Status Changed Event
        if (fine.getStatus() != FineStatus.UNPAID) {
            com.trafficfine.common.event.FineStatusChangedEvent statusEvent = new com.trafficfine.common.event.FineStatusChangedEvent(
                    fine.getReferenceNumber(),
                    "UNPAID",
                    fine.getStatus().name(),
                    fine.getPaidAt() != null ? fine.getPaidAt() : LocalDateTime.now()
            );
            rabbit.convertAndSend(com.trafficfine.common.event.RabbitMQConstants.EXCHANGE, com.trafficfine.common.event.RabbitMQConstants.ROUTING_KEY_FINE_STATUS_CHANGED, statusEvent);
        }
    }
}
