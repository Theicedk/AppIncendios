package cl.duoc.valledelsol.ms_alarmas.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import cl.duoc.valledelsol.ms_alarmas.dto.ReporteKafkaEvent;
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
            ReporteKafkaEvent reporte = objectMapper.readValue(mensaje, ReporteKafkaEvent.class);

            String mensajeAlarma = reporte.descripcion() != null
                ? reporte.descripcion()
                : ("Reporte ID: " + reporte.id());

            Severidad severidad = Severidad.ROJA;

            Alarma alarma = new Alarma(null, mensajeAlarma, reporte.id(), severidad, null);
            Alarma guardada = alarmaRepository.save(alarma);

            System.out.println("Alarma guardada con ID: " + guardada.getId());
        } catch (Exception ex) {
            System.out.println("Error guardando alarma: " + ex.getMessage());
        }
    }
}