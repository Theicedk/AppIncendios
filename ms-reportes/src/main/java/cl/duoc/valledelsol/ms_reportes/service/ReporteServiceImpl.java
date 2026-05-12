package cl.duoc.valledelsol.ms_reportes.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.geo.Point;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.valledelsol.ms_reportes.dto.ReporteDTO;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteListaDTO;
import cl.duoc.valledelsol.ms_reportes.entity.Reporte;
import cl.duoc.valledelsol.ms_reportes.enums.EstadoIncendio;
import cl.duoc.valledelsol.ms_reportes.repository.ReporteRepository;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ReporteServiceImpl(ReporteRepository reporteRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.reporteRepository = reporteRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Reporte guardarReporte(ReporteDTO dto) {
        boolean verificado = dto.verificado() != null && dto.verificado();
        EstadoIncendio estadoIncendio = dto.estadoIncendio() != null
            ? dto.estadoIncendio()
            : EstadoIncendio.REPORTADO;

        Reporte reporte = new Reporte(
            null,
            LocalDateTime.now(),
            dto.encabezado() != null ? dto.encabezado() : "Reporte de incendio",
            dto.descripcion(),
            new Point(dto.longitud(), dto.latitud()),
            verificado,
            estadoIncendio,
            null
        );

        return reporteRepository.save(reporte);
    }

    @Override
    public List<ReporteListaDTO> obtenerTodos() {
        return reporteRepository.findAll().stream()
            .map(reporte -> new ReporteListaDTO(
                reporte.getId(),
                reporte.getDescripcion()
            ))
            .toList();
    }

    @Override
    public ReporteDTO verificarReporte(Long id) {
        Reporte reporte = reporteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reporte no encontrado con ID: " + id));
        
        reporte.setVerificado(true);
        reporte.setEstadoIncendio(EstadoIncendio.VERIFICADO);
        
        Reporte guardado = reporteRepository.save(reporte);
        
        ReporteDTO dto = new ReporteDTO(
            guardado.getEncabezado(),
            guardado.getDescripcion(),
            guardado.getUbicacion() != null ? guardado.getUbicacion().getX() : null,
            guardado.getUbicacion() != null ? guardado.getUbicacion().getY() : null,
            guardado.isVerificado(),
            guardado.getEstadoIncendio()
        );

        try {
            String mensaje = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send("topic-prueba-incendio", mensaje);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar el mensaje para Kafka", e);
        }

        return dto;
    }
}