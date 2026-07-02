package cl.duoc.valledelsol.ms_alarmas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.valledelsol.ms_alarmas.entity.Alarma;
import cl.duoc.valledelsol.ms_alarmas.service.DespachoAlarmaService;

@RestController
@RequestMapping("/api/alarmas")
public class AlarmaController {

    private final DespachoAlarmaService despachoAlarmaService;

    public AlarmaController(DespachoAlarmaService despachoAlarmaService) {
        this.despachoAlarmaService = despachoAlarmaService;
    }

    @PostMapping("/disparar/{reporteId}")
    @PreAuthorize("hasAuthority('update:reportes')")
    public ResponseEntity<Alarma> disparar(@PathVariable Long reporteId,
                                           @RequestParam(defaultValue = "1") int cantidadReportes,
                                           @RequestParam(defaultValue = "") String descripcion) {
        return ResponseEntity.ok(despachoAlarmaService.disparar(reporteId, 0.0, 0.0, true,
                cantidadReportes, descripcion));
    }

    @PutMapping("/{id}/aceptar")
    @PreAuthorize("hasAuthority('accept:alarmas')")
    public ResponseEntity<Alarma> aceptar(@PathVariable Long id) {
        return ResponseEntity.ok(despachoAlarmaService.aceptar(id));
    }
}
