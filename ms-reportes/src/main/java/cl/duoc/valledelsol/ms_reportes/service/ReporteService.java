package cl.duoc.valledelsol.ms_reportes.service;

import java.util.List;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteDTO;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteCreacionDTO;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteListaDTO;
import cl.duoc.valledelsol.ms_reportes.entity.Reporte;

public interface ReporteService {
    
    // 1. Crea el reporte (Solo guarda en BD). Devuelve un DTO simple para el frontend.
    ReporteListaDTO crearReporte(ReporteCreacionDTO dto);
    
    // 2. Obtiene todos (Para el dashboard)
    List<ReporteListaDTO> obtenerTodos();
    
    // 3. Verifica el reporte (Actualiza BD Y dispara Kafka)
    ReporteDTO verificarReporte(Long id);
}