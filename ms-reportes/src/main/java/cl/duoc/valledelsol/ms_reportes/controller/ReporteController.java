package cl.duoc.valledelsol.ms_reportes.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.valledelsol.ms_reportes.dto.ReporteCreacionDTO;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteDTO;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteListaDTO;
import cl.duoc.valledelsol.ms_reportes.service.ReporteService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    // 1. El endpoint de prueba que siempre ha funcionado
    @GetMapping("/test")
    public String testReporte() {
        return "Microservicio de Reportes responde OK";
    }

    // 2. El GET para listar (El que usa el BFF Dashboard)
    @GetMapping
    public List<ReporteListaDTO> obtenerReportes() {
        return reporteService.obtenerTodos();
    }

    // 3. EL POST REAL QUE FALTABA (Crea el reporte en BD, SIN Kafka)
    @PostMapping
    public ReporteListaDTO crearReporte(@RequestBody ReporteCreacionDTO reporteDTO) {
        return reporteService.crearReporte(reporteDTO);
    }

    // 4. EL PUT DE VERIFICACIÓN (Actualiza BD Y dispara Kafka)
    @PutMapping("/{id}/verificar")
    public ReporteDTO verificarReporte(@PathVariable("id") Long id) {
        return reporteService.verificarReporte(id);
    }
}