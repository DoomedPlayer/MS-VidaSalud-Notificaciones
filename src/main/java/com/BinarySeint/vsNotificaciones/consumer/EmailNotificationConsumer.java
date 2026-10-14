package com.BinarySeint.vsNotificaciones.consumer;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

@Component 
public class EmailNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationConsumer.class);

    @RabbitListener(queues = "${rabbitmq.queues.email")
    public void processEmailMessage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        String payload = new String(message.getBody());
        log.info("📩 Mensaje recibido en q.cmd.email: {}", payload);

        try {

            if (payload.contains("ERROR_SIMULADO")) {
                throw new RuntimeException("Fallo simulado al contactar el servidor SMTP");
            }
            
            log.info("✅ Correo enviado exitosamente al paciente.");

            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("❌ Error al procesar el correo: {}. Enviando a DLQ...", e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
