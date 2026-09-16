package com.BinarySeint.vsNotificaciones.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {

    @PostMapping("/email")
    public ResponseEntity<Void> procesarEmail(@RequestBody Map<String, Object> mensajeEnvelope) {
        System.out.println("Procesando envío de Email/Push vía REST...");
        System.out.println("TraceId: " + mensajeEnvelope.get("traceId"));
        System.out.println("Datos de la atención: " + mensajeEnvelope.get("data"));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/admission")
    public ResponseEntity<Void> procesarTicketAdmision(@RequestBody Map<String, Object> mensajeEnvelope) {
        System.out.println("Generando ticket de admisión para el box clínico vía REST...");
        System.out.println("TraceId: " + mensajeEnvelope.get("traceId"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/record")
    public ResponseEntity<Void> procesarGeneracionPdf(@RequestBody Map<String, Object> mensajeEnvelope) {
        System.out.println("Generando PDF comprobante vía REST...");

        return ResponseEntity.ok().build();
    }
}