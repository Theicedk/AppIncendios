package cl.duoc.valledelsol.ms_reportes.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
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

        return mapToListaDto(guardado);
    }

    @Override
    public ReporteDTO verificarReporte(Long id) {
        Reporte reporte = buscarReporte(id);
        transicionar(reporte, EstadoIncendio.VERIFICADO);

        reporte.setVerificado(true);
        reporte.setEstadoIncendio(EstadoIncendio.VERIFICADO);
        Reporte guardado = reporteRepository.save(reporte);

        publicarEventoKafka(guardado);

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
    @Transactional
    public void atenderReporte(Long id) {
        Reporte reporte = buscarReporte(id);
        transicionar(reporte, EstadoIncendio.ATENDIDO);

        reporte.setEstadoIncendio(EstadoIncendio.ATENDIDO);
        reporteRepository.save(reporte);

        if (reporte.getGrupoId() != null) {
            reporteRepository.cerrarGrupo(reporte.getGrupoId(), EstadoIncendio.ATENDIDO, reporte.getId());
        }
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

    private void publicarEventoKafka(Reporte reporte) {
        ReporteKafkaEvent evento = new ReporteKafkaEvent(
            reporte.getId(),
            reporte.getDescripcion(),
            reporte.getLatitud(),
            reporte.getLongitud(),
            reporte.getGrupoId()
        );

        try {
            String mensajeJson = objectMapper.writeValueAsString(evento);
            kafkaTemplate.send(TOPIC_REPORTE, mensajeJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar el mensaje para Kafka", e);
        }
    }
}
