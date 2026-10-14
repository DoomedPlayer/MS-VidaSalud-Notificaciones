package com.BinarySeint.vsNotificaciones.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration 
public class RabbitMQConfig {

    @Value ("${rabbitmq.exchanges.direct}")
    private String directExchangeName;

    @Value("${rabbitmq.exchanges.dlx}")
    private String dlxExchangeName;

    @Value("${rabbitmq.queues.email}")
    private String emailQueueName;

    @Value("${rabbitmq.queues.email-dlq}")
    private String emailDlqName;

    @Value("${rabbitmq.routing.email-send}")
    private String emailRoutingKey;

    @Bean 
    public DirectExchange directExchange() {
        return new DirectExchange(directExchangeName);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(dlxExchangeName);
    }

    @Bean
    public Queue emailDlq() {
        return QueueBuilder.durable(emailDlqName).build();
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(emailQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", emailDlqName) 
                .build();
    }

    // 4. Bindings (Enlaces)
    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(emailQueue).to(directExchange).with(emailRoutingKey);
    }

    @Bean
    public Binding emailDlqBinding(Queue emailDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(emailDlq).to(dlxExchange).with(emailDlqName);
    }
}
