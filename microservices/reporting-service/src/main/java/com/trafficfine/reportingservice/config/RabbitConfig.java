package com.trafficfine.reportingservice.config;

import com.trafficfine.common.event.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(RabbitMQConstants.EXCHANGE);
    }

    // Reporting Queues
    @Bean
    public Queue fineCreatedReportingQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_FINE_CREATED_REPORTING).build();
    }

    @Bean
    public Queue fineStatusChangedReportingQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_FINE_STATUS_CHANGED_REPORTING).build();
    }

    @Bean
    public Queue paymentCompletedReportingQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_PAYMENT_COMPLETED_REPORTING).build();
    }

    // Bindings
    @Bean
    public Binding fineCreatedReportingBinding() {
        return BindingBuilder.bind(fineCreatedReportingQueue())
                .to(exchange())
                .with(RabbitMQConstants.ROUTING_KEY_FINE_CREATED);
    }

    @Bean
    public Binding fineStatusChangedReportingBinding() {
        return BindingBuilder.bind(fineStatusChangedReportingQueue())
                .to(exchange())
                .with(RabbitMQConstants.ROUTING_KEY_FINE_STATUS_CHANGED);
    }

    @Bean
    public Binding paymentCompletedReportingBinding() {
        return BindingBuilder.bind(paymentCompletedReportingQueue())
                .to(exchange())
                .with(RabbitMQConstants.ROUTING_KEY_PAYMENT_COMPLETED);
    }
}
