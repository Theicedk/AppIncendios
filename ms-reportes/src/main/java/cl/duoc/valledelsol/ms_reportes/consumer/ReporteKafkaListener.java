package cl.duoc.valledelsol.ms_reportes.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import cl.duoc.valledelsol.ms_reportes.entity.Reporte;
import cl.duoc.valledelsol.ms_reportes.service.ReporteService;

@Component
public class ReporteKafkaListener {

    private final ObjectMapper objectMapper;
    private final ReporteService reporteService;

    public ReporteKafkaListener(ObjectMapper objectMapper, ReporteService reporteService) {
        this.objectMapper = objectMapper;
        this.reporteService = reporteService;
    }

    @KafkaListener(topics = "topic-prueba-incendio", groupId = "ms-reportes-grupo-prueba")
    public void escuchar(String mensaje) throws JsonProcessingException {
        ReporteDTO reporteDTO = objectMapper.readValue(mensaje, ReporteDTO.class);

        Reporte guardado = reporteService.guardarReporte(reporteDTO);
        System.out.println("Reporte guardado con ID: " + guardado.getId());
    }
}