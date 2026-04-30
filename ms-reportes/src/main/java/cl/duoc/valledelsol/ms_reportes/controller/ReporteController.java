package cl.duoc.valledelsol.ms_reportes.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.valledelsol.ms_reportes.dto.ReporteListaDTO;
import cl.duoc.valledelsol.ms_reportes.entity.Reporte;
import cl.duoc.valledelsol.ms_reportes.repository.ReporteRepository;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteRepository reporteRepository;

    public ReporteController(ReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    @GetMapping
    public List<ReporteListaDTO> obtenerReportes() {
        List<Reporte> reportes = reporteRepository.findAll();

        return reportes.stream()
                .map(reporte -> new ReporteListaDTO(
                    reporte.getId(),
                    reporte.getDescripcion()
                ))
                .toList();
    }
}
