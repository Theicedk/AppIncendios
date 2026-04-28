package cl.duoc.valledelsol.ms_alarmas.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cl.duoc.valledelsol.ms_alarmas.entity.Alarma;
import cl.duoc.valledelsol.ms_alarmas.enums.Severidad;
import cl.duoc.valledelsol.ms_alarmas.repository.AlarmaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTestConsumer {

    private final ObjectMapper objectMapper;
    private final AlarmaRepository alarmaRepository;

    public KafkaTestConsumer(ObjectMapper objectMapper, AlarmaRepository alarmaRepository) {
        this.objectMapper = objectMapper;
        this.alarmaRepository = alarmaRepository;
    }

    @KafkaListener(topics = "topic-prueba-incendio", groupId = "ms-alarmas-grupo-prueba")
    public void escuchar(String mensaje) {
        try {
            JsonNode root = objectMapper.readTree(mensaje);

            String descripcion = root.path("descripcion").asText(mensaje);
            Long reporteId = root.hasNonNull("reporteId") ? root.get("reporteId").asLong() : null;

            Alarma alarma = new Alarma(null, descripcion, reporteId, Severidad.ROJA);
            Alarma guardada = alarmaRepository.save(alarma);

            System.out.println("Alarma guardada con ID: " + guardada.getId());
        } catch (Exception ex) {
            System.out.println("Error guardando alarma: " + ex.getMessage());
        }
    }
}