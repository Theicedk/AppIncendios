package cl.duoc.valledelsol.ms_geolocalizacion.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.valledelsol.ms_geolocalizacion.dto.HeatmapDTO;
import cl.duoc.valledelsol.ms_geolocalizacion.service.FocoService;

@RestController
@RequestMapping("/api/geolocalizacion")
public class GeolocalizacionController {

    private final FocoService focoService;

    public GeolocalizacionController(FocoService focoService) {
        this.focoService = focoService;
    }

    @GetMapping("/heatmap")
    public List<HeatmapDTO> obtenerHeatmap() {
        return focoService.heatmapAgregado();
    }
}
