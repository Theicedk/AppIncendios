package cl.duoc.valledelsol.ms_geolocalizacion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.valledelsol.ms_geolocalizacion.dto.ZonaRiesgoDTO;
import cl.duoc.valledelsol.ms_geolocalizacion.dto.ZonaRiesgoRequestDTO;
import cl.duoc.valledelsol.ms_geolocalizacion.entity.ZonaRiesgo;
import cl.duoc.valledelsol.ms_geolocalizacion.repository.ZonaRiesgoRepository;

@Service
public class ZonaRiesgoServiceImpl implements ZonaRiesgoService {

    private final ZonaRiesgoRepository repository;

    public ZonaRiesgoServiceImpl(ZonaRiesgoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ZonaRiesgoDTO> obtenerTodas() {
        return repository.findAll().stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public ZonaRiesgoDTO obtenerPorId(Long id) {
        return toDTO(buscar(id));
    }

    @Override
    @Transactional
    public ZonaRiesgoDTO crear(ZonaRiesgoRequestDTO dto) {
        ZonaRiesgo z = new ZonaRiesgo();
        z.setDescripcion(dto.descripcion());
        z.setLatitud(dto.latitud());
        z.setLongitud(dto.longitud());
        return toDTO(repository.save(z));
    }

    @Override
    @Transactional
    public ZonaRiesgoDTO actualizar(Long id, ZonaRiesgoRequestDTO dto) {
        ZonaRiesgo z = buscar(id);
        z.setDescripcion(dto.descripcion());
        z.setLatitud(dto.latitud());
        z.setLongitud(dto.longitud());
        return toDTO(repository.save(z));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Zona de riesgo no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }

    private ZonaRiesgo buscar(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Zona de riesgo no encontrada con ID: " + id));
    }

    private ZonaRiesgoDTO toDTO(ZonaRiesgo z) {
        return new ZonaRiesgoDTO(z.getId(), z.getDescripcion(), z.getLatitud(), z.getLongitud());
    }
}
