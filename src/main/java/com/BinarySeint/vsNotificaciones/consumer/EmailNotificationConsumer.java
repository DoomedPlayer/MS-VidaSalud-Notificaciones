package com.BinarySeint.vsNotificaciones.consumer;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.BinarySeint.vsNotificaciones.service.EmailSenderService;
import com.rabbitmq.client.Channel;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component 
public class EmailNotificationConsumer {

    @Autowired
    private EmailSenderService emailSenderService;

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationConsumer.class);

    @RabbitListener(queues = "${rabbitmq.queues.email}")
    public void processEmailMessage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        String payload = new String(message.getBody());
        log.info("📩 Mensaje recibido en q.cmd.email: {}", payload);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(payload);

            JsonNode dataNode = rootNode.path("data");
            String nombrePaciente = dataNode.path("nombrePaciente").asText();
            String estado = dataNode.path("estado").asText();

            String correoDestino = "ni.beeger@duocuc.cl"; 

            String asunto = "Actualización de su Cita Médica";
            String cuerpo = "Hola " + nombrePaciente + ",\n\n" +
                            "El estado de su cita ha cambiado a: " + estado + ".\n\n" +
                            "Saludos,\nClínica VidaSalud.";

            emailSenderService.enviarCorreo(correoDestino, asunto, cuerpo);

            log.info("✅ Correo enviado exitosamente a: " + correoDestino);
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("❌ Error al procesar el correo: {}. Enviando a DLQ...", e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
