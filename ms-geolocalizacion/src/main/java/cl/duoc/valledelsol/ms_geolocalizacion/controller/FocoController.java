package cl.duoc.valledelsol.ms_geolocalizacion.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.valledelsol.ms_geolocalizacion.dto.FocoMapaDTO;
import cl.duoc.valledelsol.ms_geolocalizacion.service.FocoService;

@RestController
@RequestMapping("/api/focos")
public class FocoController {

    private final FocoService focoService;

    public FocoController(FocoService focoService) {
        this.focoService = focoService;
    }

    @GetMapping
    public List<FocoMapaDTO> obtenerFocos() {
        return focoService.obtenerTodos();
    }
}
