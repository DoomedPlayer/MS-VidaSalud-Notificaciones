package com.BinarySeint.vsNotificaciones.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration 
public class RabbitMQConfig {

    @Value("${rabbitmq.exchanges.direct}")
    private String directExchangeName;

    @Value("${rabbitmq.exchanges.dlx}")
    private String dlxExchangeName;

    @Value("${rabbitmq.queues.email}") private String emailQueueName;
    @Value("${rabbitmq.queues.email-dlq}") private String emailDlqName;
    @Value("${rabbitmq.routing.email-send}") private String emailRoutingKey;

    @Value("${rabbitmq.queues.admission}") private String admissionQueueName;
    @Value("${rabbitmq.queues.admission-dlq}") private String admissionDlqName;
    @Value("${rabbitmq.routing.admission-ticket}") private String admissionRoutingKey;

    @Value("${rabbitmq.queues.record}") private String recordQueueName;
    @Value("${rabbitmq.queues.record-dlq}") private String recordDlqName;
    @Value("${rabbitmq.routing.record-gen}") private String recordRoutingKey;

    @Bean 
    public DirectExchange directExchange() {
        return new DirectExchange(directExchangeName);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(dlxExchangeName);
    }

    @Bean
    public Queue emailDlq() { return QueueBuilder.durable(emailDlqName).build(); }
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(emailQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", emailDlqName) 
                .build();
    }

    @Bean
    public Queue admissionDlq() { return QueueBuilder.durable(admissionDlqName).build(); }
    @Bean
    public Queue admissionQueue() {
        return QueueBuilder.durable(admissionQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", admissionDlqName) 
                .build();
    }

    @Bean
    public Queue recordDlq() { return QueueBuilder.durable(recordDlqName).build(); }
    @Bean
    public Queue recordQueue() {
        return QueueBuilder.durable(recordQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", recordDlqName) 
                .build();
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(emailQueue).to(directExchange).with(emailRoutingKey);
    }
    @Bean
    public Binding emailDlqBinding(Queue emailDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(emailDlq).to(dlxExchange).with(emailDlqName);
    }

    @Bean
    public Binding admissionBinding(Queue admissionQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(admissionQueue).to(directExchange).with(admissionRoutingKey);
    }
    @Bean
    public Binding admissionDlqBinding(Queue admissionDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(admissionDlq).to(dlxExchange).with(admissionDlqName);
    }

    @Bean
    public Binding recordBinding(Queue recordQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(recordQueue).to(directExchange).with(recordRoutingKey);
    }
    @Bean
    public Binding recordDlqBinding(Queue recordDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(recordDlq).to(dlxExchange).with(recordDlqName);
    }
}