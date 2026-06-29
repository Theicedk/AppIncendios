package cl.duoc.valledelsol.ms_alarmas.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.valledelsol.ms_alarmas.dto.ReporteKafkaEvent;
import cl.duoc.valledelsol.ms_alarmas.service.DespachoAlarmaService;

@Component
public class AlarmaKafkaListener {

    private final ObjectMapper objectMapper;
    private final DespachoAlarmaService despachoAlarmaService;

    public AlarmaKafkaListener(ObjectMapper objectMapper, DespachoAlarmaService despachoAlarmaService) {
        this.objectMapper = objectMapper;
        this.despachoAlarmaService = despachoAlarmaService;
    }

    @KafkaListener(topics = "topic-prueba-incendio", groupId = "ms-alarmas")
    public void escuchar(String mensaje) {
        try {
            ReporteKafkaEvent evento = objectMapper.readValue(mensaje, ReporteKafkaEvent.class);
            if (evento.latitud() != null && evento.longitud() != null) {
                despachoAlarmaService.disparar(evento.id(), evento.latitud(), evento.longitud(), true);
            }
        } catch (Exception ex) {
            System.err.println("Error procesando evento Kafka: " + ex.getMessage());
        }
    }
}
