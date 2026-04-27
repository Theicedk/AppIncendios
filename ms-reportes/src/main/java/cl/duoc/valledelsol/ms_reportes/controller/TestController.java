package cl.duoc.valledelsol.ms_reportes.controller;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public TestController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/api/reportes/test")
    public String testReporte() {
        return "Microservicio de Reportes responde OK";
    }

    @GetMapping("/api/reportes/kafka-test")
    public String kafkaTest() {
        String mensaje = "Evento de prueba desde ms-reportes";
        kafkaTemplate.send("topic-prueba-incendio", mensaje);
        return "Mensaje enviado a Kafka: " + mensaje;
    }
}