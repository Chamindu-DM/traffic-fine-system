package com.trafficfine.common.event;

/**
 * Shared RabbitMQ constants used across all microservices.
 * Defines exchange names, queue names, and routing keys for the event-driven architecture.
 */
public final class RabbitMQConstants {

    private RabbitMQConstants() {
        // Utility class — prevent instantiation
    }

    // ── Exchange ──
    public static final String EXCHANGE = "traffic-fine.exchange";

    // ── Routing Keys ──
    public static final String ROUTING_KEY_FINE_CREATED = "fine.created";
    public static final String ROUTING_KEY_FINE_STATUS_CHANGED = "fine.status.changed";
    public static final String ROUTING_KEY_PAYMENT_COMPLETED = "payment.completed";
    public static final String ROUTING_KEY_PAYMENT_FAILED = "payment.failed";

    // ── Queues ──
    public static final String QUEUE_FINE_CREATED_REPORTING = "fine.created.reporting";
    public static final String QUEUE_FINE_STATUS_CHANGED_REPORTING = "fine.status.changed.reporting";
    public static final String QUEUE_PAYMENT_COMPLETED_NOTIFICATION = "payment.completed.notification";
    public static final String QUEUE_PAYMENT_COMPLETED_REPORTING = "payment.completed.reporting";

    // ── Dead Letter Queues ──
    public static final String DLQ_PAYMENT_COMPLETED_NOTIFICATION = "payment.completed.notification.dlq";
    public static final String DLX_PAYMENT_COMPLETED_NOTIFICATION = "payment.completed.notification.dlx";
    public static final String DLK_PAYMENT_COMPLETED_NOTIFICATION = "payment.completed.notification.dlk";
}
