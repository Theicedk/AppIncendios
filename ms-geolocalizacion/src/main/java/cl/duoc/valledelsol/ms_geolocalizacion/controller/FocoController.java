package cl.duoc.valledelsol.ms_geolocalizacion.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.valledelsol.ms_geolocalizacion.dto.FocoMapaDTO;
import cl.duoc.valledelsol.ms_geolocalizacion.entity.FocoIncendio;
import cl.duoc.valledelsol.ms_geolocalizacion.repository.FocoIncendioRepository;

@RestController
@RequestMapping("/api/focos")
public class FocoController {

    private final FocoIncendioRepository focoIncendioRepository;

    public FocoController(FocoIncendioRepository focoIncendioRepository) {
        this.focoIncendioRepository = focoIncendioRepository;
    }

    @GetMapping
    public List<FocoMapaDTO> obtenerFocos() {
        List<FocoIncendio> focos = focoIncendioRepository.findAll();

        return focos.stream()
                .map(foco -> new FocoMapaDTO(
                    foco.getId(),
                    foco.getUbicacion().getY(),    // y = latitud
                    foco.getUbicacion().getX(),    // x = longitud
                    "ACTIVO"                       // estado fijo por ahora
                ))
                .toList();
    }
}
