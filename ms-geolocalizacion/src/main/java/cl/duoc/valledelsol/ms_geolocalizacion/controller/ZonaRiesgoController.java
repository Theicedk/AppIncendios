package cl.duoc.valledelsol.ms_geolocalizacion.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.valledelsol.ms_geolocalizacion.dto.ZonaRiesgoDTO;
import cl.duoc.valledelsol.ms_geolocalizacion.dto.ZonaRiesgoRequestDTO;
import cl.duoc.valledelsol.ms_geolocalizacion.service.ZonaRiesgoService;

@RestController
@RequestMapping("/api/zonas-riesgo")
public class ZonaRiesgoController {

    private final ZonaRiesgoService service;

    public ZonaRiesgoController(ZonaRiesgoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ZonaRiesgoDTO> obtenerTodas() {
        return service.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ZonaRiesgoDTO obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public ZonaRiesgoDTO crear(@RequestBody ZonaRiesgoRequestDTO dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public ZonaRiesgoDTO actualizar(@PathVariable Long id, @RequestBody ZonaRiesgoRequestDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
