package cl.duoc.valledelsol.ms_geolocalizacion.consumer;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.valledelsol.ms_geolocalizacion.dto.ReporteKafkaEvent;
import cl.duoc.valledelsol.ms_geolocalizacion.entity.FocoIncendio;
import cl.duoc.valledelsol.ms_geolocalizacion.repository.FocoIncendioRepository;

@Component
public class FocoIncendioKafkaListener {

    private final ObjectMapper objectMapper;
    private final GeometryFactory geometryFactory;
    private final FocoIncendioRepository focoIncendioRepository;

    public FocoIncendioKafkaListener(ObjectMapper objectMapper,
                                     GeometryFactory geometryFactory,
                                     FocoIncendioRepository focoIncendioRepository) {
        this.objectMapper = objectMapper;
        this.geometryFactory = geometryFactory;
        this.focoIncendioRepository = focoIncendioRepository;
    }

    @KafkaListener(topics = "topic-prueba-incendio", groupId = "ms-geolocalizacion")
    public void escucharFoco(String mensaje) {
        try {
            ReporteKafkaEvent dto = objectMapper.readValue(mensaje, ReporteKafkaEvent.class);

            if (dto.latitud() == null || dto.longitud() == null) {
                throw new IllegalArgumentException("Latitud y longitud son obligatorias para crear el Point");
            }

            Point ubicacion = geometryFactory.createPoint(new Coordinate(dto.longitud(), dto.latitud()));
            ubicacion.setSRID(4326);

            FocoIncendio focoIncendio = new FocoIncendio();
            focoIncendio.setReporteId(dto.id());
            focoIncendio.setUbicacion(ubicacion);

            focoIncendioRepository.save(focoIncendio);
            System.out.println("Foco de incendio guardado correctamente con ID de reporte: " + dto.id());
        } catch (Exception e) {
            System.err.println("Error procesando mensaje en FocoIncendioKafkaListener:");
            e.printStackTrace();
        }
    }
}