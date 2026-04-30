package cl.duoc.valledelsol.ms_geolocalizacion.consumer;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.valledelsol.ms_geolocalizacion.dto.ReporteIncendioKafkaDTO;
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
    public void escucharFoco(String mensaje) throws Exception {
        ReporteIncendioKafkaDTO dto = objectMapper.readValue(mensaje, ReporteIncendioKafkaDTO.class);

        if (dto.latitud() == null || dto.longitud() == null) {
            throw new IllegalArgumentException("Latitud y longitud son obligatorias para crear el Point");
        }

        Point ubicacion = geometryFactory.createPoint(new Coordinate(dto.longitud(), dto.latitud()));
        ubicacion.setSRID(4326);

        FocoIncendio focoIncendio = new FocoIncendio();
        focoIncendio.setReporteId(dto.reporteId());
        focoIncendio.setUbicacion(ubicacion);

        focoIncendioRepository.save(focoIncendio);
    }
}