package cl.duoc.valledelsol.ms_reportes.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.valledelsol.ms_reportes.dto.ReporteCreacionDTO;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteDTO;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteKafkaEvent;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteListaDTO;
import cl.duoc.valledelsol.ms_reportes.entity.Reporte;
import cl.duoc.valledelsol.ms_reportes.enums.EstadoIncendio;
import cl.duoc.valledelsol.ms_reportes.repository.ReporteRepository;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ReporteServiceImpl(ReporteRepository reporteRepository, 
                              KafkaTemplate<String, String> kafkaTemplate, 
                              ObjectMapper objectMapper) {
        this.reporteRepository = reporteRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public ReporteListaDTO crearReporte(ReporteCreacionDTO dto) {
        Reporte reporte = new Reporte();
        reporte.setFecha(LocalDateTime.now());
        reporte.setEncabezado("Reporte Ciudadano");
        reporte.setDescripcion(dto.descripcion());
        reporte.setVerificado(false);
        reporte.setEstadoIncendio(EstadoIncendio.REPORTADO);
        
        reporte.setLatitud(dto.latitud());
        reporte.setLongitud(dto.longitud());

        Reporte guardado = reporteRepository.save(reporte);

        return new ReporteListaDTO(
            guardado.getId(),
            guardado.getDescripcion(),
            guardado.getLatitud(),
            guardado.getLongitud(),
            guardado.getEstadoIncendio().name()
        );
    }

    @Override
    public List<ReporteListaDTO> obtenerTodos() {
        return reporteRepository.findAll().stream()
            .map(reporte -> new ReporteListaDTO(
                reporte.getId(),
                reporte.getDescripcion(),
                reporte.getLatitud(),
                reporte.getLongitud(),
                reporte.getEstadoIncendio() != null ? reporte.getEstadoIncendio().name() : EstadoIncendio.REPORTADO.name()
            ))
            .toList();
    }

    @Override
    public ReporteDTO verificarReporte(Long id) {
        Reporte reporte = reporteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reporte no encontrado con ID: " + id));
        
        reporte.setVerificado(true);
        reporte.setEstadoIncendio(EstadoIncendio.ACTIVO);
        Reporte guardado = reporteRepository.save(reporte);

        Double lat = guardado.getLatitud();
        Double lon = guardado.getLongitud();

        ReporteKafkaEvent evento = new ReporteKafkaEvent(
            guardado.getId(), 
            guardado.getDescripcion(), 
            lat, 
            lon
        );

        try {
            String mensajeJson = objectMapper.writeValueAsString(evento);
            kafkaTemplate.send("topic-prueba-incendio", mensajeJson);
            System.out.println("🚨 ALERTA DISPARADA POR KAFKA PARA EL REPORTE ID: " + id + " | LAT: " + lat + " LON: " + lon);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar el mensaje para Kafka", e);
        }

        return new ReporteDTO(
            guardado.getEncabezado(),
            guardado.getDescripcion(),
            lat,
            lon,
            guardado.isVerificado(),
            guardado.getEstadoIncendio()
        );
    }
}
