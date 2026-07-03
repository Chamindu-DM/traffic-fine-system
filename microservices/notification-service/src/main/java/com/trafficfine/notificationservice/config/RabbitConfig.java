package com.trafficfine.notificationservice.config;

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

    // Dead Letter Exchange
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(RabbitMQConstants.DLX_PAYMENT_COMPLETED_NOTIFICATION);
    }

    // Dead Letter Queue
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(RabbitMQConstants.DLQ_PAYMENT_COMPLETED_NOTIFICATION).build();
    }

    // Bind Dead Letter Queue to Dead Letter Exchange
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(RabbitMQConstants.DLK_PAYMENT_COMPLETED_NOTIFICATION);
    }

    // Main Queue with DLQ arguments
    @Bean
    public Queue mainQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_PAYMENT_COMPLETED_NOTIFICATION)
                .withArgument("x-dead-letter-exchange", RabbitMQConstants.DLX_PAYMENT_COMPLETED_NOTIFICATION)
                .withArgument("x-dead-letter-routing-key", RabbitMQConstants.DLK_PAYMENT_COMPLETED_NOTIFICATION)
                .build();
    }

    // Bind Main Queue to Main Exchange
    @Bean
    public Binding mainBinding() {
        return BindingBuilder.bind(mainQueue())
                .to(exchange())
                .with(RabbitMQConstants.ROUTING_KEY_PAYMENT_COMPLETED);
    }
}
