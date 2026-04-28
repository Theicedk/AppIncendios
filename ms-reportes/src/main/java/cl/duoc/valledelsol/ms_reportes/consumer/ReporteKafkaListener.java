package cl.duoc.valledelsol.ms_reportes.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteDTO;
import cl.duoc.valledelsol.ms_reportes.entity.Reporte;
import cl.duoc.valledelsol.ms_reportes.enums.EstadoIncendio;
import cl.duoc.valledelsol.ms_reportes.repository.ReporteRepository;
import java.time.LocalDateTime;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;

@Component
public class ReporteKafkaListener {

    private final ObjectMapper objectMapper;
    private final ReporteRepository reporteRepository;

    public ReporteKafkaListener(ObjectMapper objectMapper, ReporteRepository reporteRepository) {
        this.objectMapper = objectMapper;
        this.reporteRepository = reporteRepository;
    }

    @KafkaListener(topics = "topic-prueba-incendio", groupId = "ms-reportes-grupo-prueba")
    public void escuchar(String mensaje) throws JsonProcessingException {
        ReporteDTO reporteDTO = objectMapper.readValue(mensaje, ReporteDTO.class);

        boolean verificado = reporteDTO.verificado() != null && reporteDTO.verificado();
        EstadoIncendio estadoIncendio = reporteDTO.estadoIncendio() != null
            ? reporteDTO.estadoIncendio()
            : EstadoIncendio.REPORTADO;

        Reporte reporte = new Reporte(
            null,
            LocalDateTime.now(),
            reporteDTO.encabezado() != null ? reporteDTO.encabezado() : "Reporte de incendio",
            reporteDTO.descripcion(),
            new Point(reporteDTO.longitud(), reporteDTO.latitud()),
            verificado,
            estadoIncendio
        );

        Reporte guardado = reporteRepository.save(reporte);
        System.out.println("Reporte guardado con ID: " + guardado.getId());
    }
}