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

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component 
public class AdmisionConsumer {

    private static final Logger log = LoggerFactory.getLogger(AdmisionConsumer.class);

    @RabbitListener(queues = "${rabbitmq.queues.admission}")
    public void processAdmissionMessage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        String payload = new String(message.getBody());
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(payload);
            JsonNode dataNode = rootNode.path("data");
            
            String nombrePaciente = dataNode.path("nombrePaciente").asText();
            String estado = dataNode.path("estado").asText();

            log.info("==================================================");
            log.info("🎫 TICKET DE ADMISIÓN / LLAMADO A BOX");
            log.info("Paciente : {}", nombrePaciente);
            log.info("Estado   : {}", estado);
            log.info("Acción   : Mostrando en pantalla de sala de espera...");
            log.info("==================================================");

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("❌ Error en admisión. Enviando a DLQ...", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
