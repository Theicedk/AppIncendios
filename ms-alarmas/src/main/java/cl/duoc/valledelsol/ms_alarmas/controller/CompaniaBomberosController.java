package cl.duoc.valledelsol.ms_alarmas.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.valledelsol.ms_alarmas.dto.CompaniaDTO;
import cl.duoc.valledelsol.ms_alarmas.repository.CompaniaBomberosRepository;

@RestController
@RequestMapping("/api/companias")
@CrossOrigin(originPatterns = "*")
public class CompaniaBomberosController {

    private final CompaniaBomberosRepository companiaRepository;

    public CompaniaBomberosController(CompaniaBomberosRepository companiaRepository) {
        this.companiaRepository = companiaRepository;
    }

    @GetMapping
    public List<CompaniaDTO> listarActivas() {
        return companiaRepository.findByActivaTrue().stream()
            .map(c -> new CompaniaDTO(
                c.getId(),
                c.getNombre(),
                c.getLatitud() != null ? c.getLatitud() : 0.0,
                c.getLongitud() != null ? c.getLongitud() : 0.0,
                c.isActiva()
            ))
            .toList();
    }
}
