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
public class RecordConsumer {

    private static final Logger log = LoggerFactory.getLogger(RecordConsumer.class);

    @RabbitListener(queues = "${rabbitmq.queues.record}")
    public void processRecordMessage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        String payload = new String(message.getBody());
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(payload);
            JsonNode dataNode = rootNode.path("data");
            
            String nombrePaciente = dataNode.path("nombrePaciente").asText();
            long idAtencion = dataNode.path("id").asLong();

            log.info("==================================================");
            log.info("📄 GENERANDO COMPROBANTE DE ATENCIÓN (PDF)");
            log.info("Atención N° : {}", idAtencion);
            log.info("Paciente    : {}", nombrePaciente);
            log.info("Acción      : Guardando documento en AWS S3...");
            log.info("==================================================");

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("❌ Error generando documento. Enviando a DLQ...", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
