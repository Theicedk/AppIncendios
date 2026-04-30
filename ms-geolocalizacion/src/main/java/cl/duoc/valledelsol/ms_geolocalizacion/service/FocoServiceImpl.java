package cl.duoc.valledelsol.ms_geolocalizacion.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.valledelsol.ms_geolocalizacion.dto.FocoMapaDTO;
import cl.duoc.valledelsol.ms_geolocalizacion.entity.FocoIncendio;
import cl.duoc.valledelsol.ms_geolocalizacion.repository.FocoIncendioRepository;

@Service
public class FocoServiceImpl implements FocoService {

    private final FocoIncendioRepository focoIncendioRepository;

    public FocoServiceImpl(FocoIncendioRepository focoIncendioRepository) {
        this.focoIncendioRepository = focoIncendioRepository;
    }

    @Override
    public List<FocoMapaDTO> obtenerTodos() {
        List<FocoIncendio> focos = focoIncendioRepository.findAll();

        return focos.stream()
                .map(foco -> new FocoMapaDTO(
                    foco.getId(),
                    foco.getUbicacion().getY(),
                    foco.getUbicacion().getX(),
                    "ACTIVO"
                ))
                .toList();
    }
}