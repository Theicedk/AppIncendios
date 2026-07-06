package cl.duoc.valledelsol.ms_alarmas.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.valledelsol.ms_alarmas.dto.CompaniaDTO;
import cl.duoc.valledelsol.ms_alarmas.dto.CompaniaRequestDTO;
import cl.duoc.valledelsol.ms_alarmas.service.CompaniaBomberosService;

@RestController
@RequestMapping("/api/companias")
@CrossOrigin(originPatterns = "*")
public class CompaniaBomberosController {

    private final CompaniaBomberosService companiaService;

    public CompaniaBomberosController(CompaniaBomberosService companiaService) {
        this.companiaService = companiaService;
    }

    @GetMapping
    public List<CompaniaDTO> listarActivas() {
        return companiaService.listarActivas();
    }

    @GetMapping("/{id}")
    public CompaniaDTO obtenerPorId(@PathVariable Long id) {
        return companiaService.obtenerPorId(id);
    }

    @PostMapping
    public CompaniaDTO crear(@RequestBody CompaniaRequestDTO dto) {
        return companiaService.crear(dto);
    }

    @PutMapping("/{id}")
    public CompaniaDTO actualizar(@PathVariable Long id, @RequestBody CompaniaRequestDTO dto) {
        return companiaService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        companiaService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
