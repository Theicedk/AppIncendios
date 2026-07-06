package cl.duoc.valledelsol.ms_reportes.service;

import java.util.List;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteDTO;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteCreacionDTO;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteListaDTO;


public interface ReporteService {
    
    ReporteListaDTO crearReporte(ReporteCreacionDTO dto);
    
    List<ReporteListaDTO> obtenerTodos();

    ReporteListaDTO iniciarCorroboracion(Long id);
    
    ReporteDTO verificarReporte(Long id);

    ReporteListaDTO obtenerPorId(Long id);

    void atenderReporte(Long id);

    void eliminarReporte(Long id);

    List<ReporteListaDTO> obtenerCercanos(double lat, double lng, double radioKm);
}