package cl.duoc.valledelsol.ms_reportes.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.valledelsol.ms_reportes.dto.ReporteCreacionDTO;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteDTO;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteKafkaEvent;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteListaDTO;
import cl.duoc.valledelsol.ms_reportes.entity.Reporte;
import cl.duoc.valledelsol.ms_reportes.enums.EstadoIncendio;
import cl.duoc.valledelsol.ms_reportes.exception.IllegalStateTransitionException;
import cl.duoc.valledelsol.ms_reportes.repository.ReporteRepository;

@Service
public class ReporteServiceImpl implements ReporteService {

    private static final String TOPIC_REPORTE = "topic-prueba-incendio";

    private final ReporteRepository reporteRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public ReporteServiceImpl(ReporteRepository reporteRepository,
                              KafkaTemplate<String, String> kafkaTemplate,
                              ObjectMapper objectMapper,
                              SimpMessagingTemplate messagingTemplate) {
        this.reporteRepository = reporteRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.messagingTemplate = messagingTemplate;
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

        notificarDashboard(mapToListaDto(guardado), "CREADO");

        return mapToListaDto(guardado);
    }

    @Override
    public List<ReporteListaDTO> obtenerTodos() {
        return reporteRepository.findAll().stream()
            .map(this::mapToListaDto)
            .toList();
    }

    @Override
    public ReporteListaDTO iniciarCorroboracion(Long id) {
        Reporte reporte = buscarReporte(id);
        transicionar(reporte, EstadoIncendio.EN_CORROBORACION);

        reporte.setEstadoIncendio(EstadoIncendio.EN_CORROBORACION);
        Reporte guardado = reporteRepository.save(reporte);

        notificarDashboard(mapToListaDto(guardado), "CORROBORADO");

        return mapToListaDto(guardado);
    }

    @Override
    public ReporteDTO verificarReporte(Long id) {
        Reporte reporte = buscarReporte(id);
        transicionar(reporte, EstadoIncendio.VERIFICADO);

        reporte.setVerificado(true);
        reporte.setEstadoIncendio(EstadoIncendio.VERIFICADO);
        Reporte guardado = reporteRepository.save(reporte);

        publicarEventoKafka(guardado, "VERIFICADO");

        notificarDashboard(mapToListaDto(guardado), "VERIFICADO");

        return new ReporteDTO(
            guardado.getEncabezado(),
            guardado.getDescripcion(),
            guardado.getLatitud(),
            guardado.getLongitud(),
            guardado.isVerificado(),
            guardado.getEstadoIncendio()
        );
    }

    @Override
    public ReporteListaDTO obtenerPorId(Long id) {
        Reporte reporte = buscarReporte(id);
        return mapToListaDto(reporte);
    }

    @Override
    @Transactional
    public void atenderReporte(Long id) {
        Reporte reporte = buscarReporte(id);
        transicionar(reporte, EstadoIncendio.ATENDIDO);

        reporte.setEstadoIncendio(EstadoIncendio.ATENDIDO);
        reporteRepository.save(reporte);

        if (reporte.getGrupoId() != null) {
            reporteRepository.cerrarGrupo(reporte.getGrupoId(), EstadoIncendio.ATENDIDO, reporte.getId());
        }

        publicarEventoKafka(reporte, "ATENDIDO");

        notificarDashboard(mapToListaDto(reporte), "ATENDIDO");
    }

    @Override
    @Transactional
    public void eliminarReporte(Long id) {
        if (!reporteRepository.existsById(id)) {
            throw new RuntimeException("Reporte no encontrado con ID: " + id);
        }
        reporteRepository.deleteById(id);
    }

    @Override
    public List<ReporteListaDTO> obtenerCercanos(double lat, double lng, double radioKm) {
        double deltaLat = radioKm / 111.0;
        double deltaLng = radioKm / (111.0 * Math.cos(Math.toRadians(lat)));
        double minLat = lat - deltaLat;
        double maxLat = lat + deltaLat;
        double minLng = lng - deltaLng;
        double maxLng = lng + deltaLng;
        return reporteRepository.findByBoundingBox(minLat, maxLat, minLng, maxLng)
            .stream()
            .map(this::mapToListaDto)
            .toList();
    }

    private Reporte buscarReporte(Long id) {
        return reporteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reporte no encontrado con ID: " + id));
    }

    private void transicionar(Reporte reporte, EstadoIncendio destino) {
        EstadoIncendio actual = reporte.getEstadoIncendio();
        if (actual == null || !actual.puedeTransicionarA(destino)) {
            throw new IllegalStateTransitionException(actual, destino);
        }
    }

    private ReporteListaDTO mapToListaDto(Reporte reporte) {
        return new ReporteListaDTO(
            reporte.getId(),
            reporte.getDescripcion(),
            reporte.getLatitud(),
            reporte.getLongitud(),
            reporte.getEstadoIncendio() != null ? reporte.getEstadoIncendio().name() : EstadoIncendio.REPORTADO.name(),
            reporte.isVerificado()
        );
    }

    private void publicarEventoKafka(Reporte reporte, String accion) {
        ReporteKafkaEvent evento = new ReporteKafkaEvent(
            reporte.getId(),
            reporte.getDescripcion(),
            reporte.getLatitud(),
            reporte.getLongitud(),
            reporte.getGrupoId(),
            accion
        );

        try {
            String mensajeJson = objectMapper.writeValueAsString(evento);
            kafkaTemplate.send(TOPIC_REPORTE, mensajeJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar el mensaje para Kafka", e);
        }
    }

    private void notificarDashboard(ReporteListaDTO dto, String accion) {
        try {
            String mensajeJson = objectMapper.writeValueAsString(dto);
            messagingTemplate.convertAndSend("/topic/reportes-municipio", mensajeJson);
        } catch (JsonProcessingException e) {
            System.err.println("Error al notificar Dashboard vía WebSocket: " + e.getMessage());
        }
    }
}
