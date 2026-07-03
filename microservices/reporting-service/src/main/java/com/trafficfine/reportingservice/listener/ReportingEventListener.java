package com.trafficfine.reportingservice.listener;

import com.trafficfine.common.event.FineCreatedEvent;
import com.trafficfine.common.event.FineStatusChangedEvent;
import com.trafficfine.common.event.RabbitMQConstants;
import com.trafficfine.reportingservice.entity.TrafficFineReport;
import com.trafficfine.reportingservice.repository.TrafficFineReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReportingEventListener {

    private static final Logger log = LoggerFactory.getLogger(ReportingEventListener.class);

    private final TrafficFineReportRepository repository;

    public ReportingEventListener(TrafficFineReportRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_FINE_CREATED_REPORTING)
    public void onFineCreated(FineCreatedEvent event) {
        log.info("Reporting Service: Received FineCreatedEvent for: {}", event.referenceNumber());
        if (repository.findByReferenceNumber(event.referenceNumber()).isPresent()) {
            return; // Avoid duplicates
        }

        TrafficFineReport report = new TrafficFineReport(
                event.referenceNumber(),
                event.categoryCode(),
                event.categoryCode(), // Use categoryCode as categoryName for simplicity
                event.amount(),
                event.district(),
                event.officerName(),
                "UNPAID",
                event.issuedAt()
        );
        repository.save(report);
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_FINE_STATUS_CHANGED_REPORTING)
    public void onFineStatusChanged(FineStatusChangedEvent event) {
        log.info("Reporting Service: Received FineStatusChangedEvent for: {} ({} -> {})",
                event.referenceNumber(), event.oldStatus(), event.newStatus());

        repository.findByReferenceNumber(event.referenceNumber()).ifPresent(report -> {
            report.setStatus(event.newStatus());
            if ("PAID".equalsIgnoreCase(event.newStatus())) {
                report.setPaidAt(event.changedAt());
            }
            repository.save(report);
        });
    }
}
