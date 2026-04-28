package cl.duoc.valledelsol.ms_reportes.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
public class TestController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TestController(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/test")
    public String testReporte() {
        return "Microservicio de Reportes responde OK";
    }

    @GetMapping("/kafka-test")
    public String kafkaTest() throws JsonProcessingException {
        ReporteDTO reporteDTO = new ReporteDTO(
            "Evento de prueba desde ms-reportes",
            -33.4489,
            -70.6693
        );

        return enviarALaCola(reporteDTO);
    }

    @PostMapping("/test")
    public String publicarReporte(@RequestBody ReporteDTO reporteDTO) throws JsonProcessingException {
        return enviarALaCola(reporteDTO);
    }

    private String enviarALaCola(ReporteDTO reporteDTO) throws JsonProcessingException {
        String mensaje = objectMapper.writeValueAsString(reporteDTO);
        kafkaTemplate.send("topic-prueba-incendio", mensaje);
        return "Mensaje enviado a Kafka: " + mensaje;
    }
}