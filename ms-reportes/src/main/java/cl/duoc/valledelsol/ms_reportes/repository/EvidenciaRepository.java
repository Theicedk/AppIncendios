package cl.duoc.valledelsol.ms_reportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.valledelsol.ms_reportes.entity.Evidencia;

@Repository
public interface EvidenciaRepository extends JpaRepository<Evidencia, Long> {
}
