package cl.duoc.valledelsol.ms_alarmas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.valledelsol.ms_alarmas.dto.CompaniaDTO;
import cl.duoc.valledelsol.ms_alarmas.dto.CompaniaRequestDTO;
import cl.duoc.valledelsol.ms_alarmas.entity.CompaniaBomberos;
import cl.duoc.valledelsol.ms_alarmas.repository.CompaniaBomberosRepository;

@Service
public class CompaniaBomberosService {

    private final CompaniaBomberosRepository companiaRepository;

    public CompaniaBomberosService(CompaniaBomberosRepository companiaRepository) {
        this.companiaRepository = companiaRepository;
    }

    public List<CompaniaDTO> listarActivas() {
        return companiaRepository.findByActivaTrue().stream()
            .map(this::toDTO)
            .toList();
    }

    public CompaniaDTO obtenerPorId(Long id) {
        CompaniaBomberos c = companiaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Compañía no encontrada con ID: " + id));
        return toDTO(c);
    }

    @Transactional
    public CompaniaDTO crear(CompaniaRequestDTO dto) {
        CompaniaBomberos compania = new CompaniaBomberos();
        compania.setNombre(dto.nombre());
        compania.setLatitud(dto.lat());
        compania.setLongitud(dto.lng());
        compania.setActiva(dto.activa());
        return toDTO(companiaRepository.save(compania));
    }

    @Transactional
    public CompaniaDTO actualizar(Long id, CompaniaRequestDTO dto) {
        CompaniaBomberos c = companiaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Compañía no encontrada con ID: " + id));
        c.setNombre(dto.nombre());
        c.setLatitud(dto.lat());
        c.setLongitud(dto.lng());
        c.setActiva(dto.activa());
        return toDTO(companiaRepository.save(c));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!companiaRepository.existsById(id)) {
            throw new RuntimeException("Compañía no encontrada con ID: " + id);
        }
        companiaRepository.deleteById(id);
    }

    private CompaniaDTO toDTO(CompaniaBomberos c) {
        return new CompaniaDTO(
            c.getId(),
            c.getNombre(),
            c.getLatitud() != null ? c.getLatitud() : 0.0,
            c.getLongitud() != null ? c.getLongitud() : 0.0,
            c.isActiva()
        );
    }
}
