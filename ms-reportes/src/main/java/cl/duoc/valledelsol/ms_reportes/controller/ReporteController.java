package cl.duoc.valledelsol.ms_reportes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    @GetMapping("/test")
    public String testReporte() {
        return "Microservicio de Reportes responde OK";
    }

    @GetMapping
    public List<ReporteListaDTO> obtenerReportes() {
        return reporteService.obtenerTodos();
    }

    
    @PostMapping
    public ReporteListaDTO crearReporte(@RequestBody ReporteCreacionDTO reporteDTO) {
        return reporteService.crearReporte(reporteDTO);
    }

    @PutMapping("/{id}/corroborar")
    public ReporteListaDTO iniciarCorroboracion(@PathVariable("id") Long id) {
        return reporteService.iniciarCorroboracion(id);
    }

    @PutMapping("/{id}/verificar")
    public ReporteDTO verificarReporte(@PathVariable("id") Long id) {
        return reporteService.verificarReporte(id);
    }

    @PutMapping("/{id}/atender")
    public ResponseEntity<Void> atenderReporte(@PathVariable("id") Long id) {
        reporteService.atenderReporte(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}