package cl.duoc.valledelsol.ms_geolocalizacion.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.valledelsol.ms_geolocalizacion.dto.ReporteKafkaEvent;
import cl.duoc.valledelsol.ms_geolocalizacion.service.FocoService;

@Component
public class FocoIncendioKafkaListener {

    private final ObjectMapper objectMapper;
    private final FocoService focoService;

    public FocoIncendioKafkaListener(ObjectMapper objectMapper, FocoService focoService) {
        this.objectMapper = objectMapper;
        this.focoService = focoService;
    }

    @KafkaListener(topics = "topic-prueba-incendio", groupId = "ms-geolocalizacion")
    public void escucharFoco(String mensaje) {
        try {
            ReporteKafkaEvent dto = objectMapper.readValue(mensaje, ReporteKafkaEvent.class);

            if (dto.latitud() == null || dto.longitud() == null) {
                throw new IllegalArgumentException("Latitud y longitud son obligatorias para crear el Point");
            }

            focoService.asignarGrupo(dto.id(), dto.latitud(), dto.longitud());
        } catch (Exception e) {
            System.err.println("Error procesando mensaje en FocoIncendioKafkaListener:");
            e.printStackTrace();
        }
    }
}