package cl.duoc.valledelsol.ms_reportes.service;

import java.util.List;

import cl.duoc.valledelsol.ms_reportes.dto.ReporteDTO;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteListaDTO;
import cl.duoc.valledelsol.ms_reportes.entity.Reporte;

public interface ReporteService {

    Reporte guardarReporte(ReporteDTO dto);

    ReporteDTO verificarReporte(Long id);

    List<ReporteListaDTO> obtenerTodos();
}