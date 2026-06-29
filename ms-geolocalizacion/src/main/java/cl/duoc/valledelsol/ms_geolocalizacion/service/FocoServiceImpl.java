package cl.duoc.valledelsol.ms_geolocalizacion.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.valledelsol.ms_geolocalizacion.dto.FocoMapaDTO;
import cl.duoc.valledelsol.ms_geolocalizacion.entity.FocoIncendio;
import cl.duoc.valledelsol.ms_geolocalizacion.repository.FocoIncendioRepository;

@Service
public class FocoServiceImpl implements FocoService {

    private static final int RADIO_EMPAQUETADO_METROS = 100;

    private final FocoIncendioRepository focoIncendioRepository;
    private final GeometryFactory geometryFactory;

    public FocoServiceImpl(FocoIncendioRepository focoIncendioRepository, GeometryFactory geometryFactory) {
        this.focoIncendioRepository = focoIncendioRepository;
        this.geometryFactory = geometryFactory;
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

    @Override
    @Transactional
    public ResultadoCluster asignarGrupo(Long reporteId, Double lat, Double lon) {
        Point ubicacion = geometryFactory.createPoint(new Coordinate(lon, lat));
        ubicacion.setSRID(4326);

        Optional<FocoIncendio> cercano = focoIncendioRepository.buscarCercano(lat, lon, RADIO_EMPAQUETADO_METROS);

        if (cercano.isPresent()) {
            FocoIncendio foco = new FocoIncendio();
            foco.setReporteId(reporteId);
            foco.setGrupoId(cercano.get().getGrupoId() != null ? cercano.get().getGrupoId() : cercano.get().getId());
            foco.setFechaCreacion(LocalDateTime.now());
            foco.setUbicacion(ubicacion);

            focoIncendioRepository.save(foco);
            return new ResultadoCluster(foco.getGrupoId(), false);
        }

        FocoIncendio nuevoFoco = new FocoIncendio();
        nuevoFoco.setReporteId(reporteId);
        nuevoFoco.setGrupoId(null);
        nuevoFoco.setFechaCreacion(LocalDateTime.now());
        nuevoFoco.setUbicacion(ubicacion);

        FocoIncendio persistido = focoIncendioRepository.save(nuevoFoco);
        persistido.setGrupoId(persistido.getId());
        focoIncendioRepository.save(persistido);

        return new ResultadoCluster(persistido.getGrupoId(), true);
    }

    public record ResultadoCluster(Long grupoId, boolean esNuevo) {
    }
}