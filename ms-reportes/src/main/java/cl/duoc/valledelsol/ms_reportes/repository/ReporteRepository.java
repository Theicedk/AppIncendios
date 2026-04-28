package cl.duoc.valledelsol.ms_reportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.valledelsol.ms_reportes.entity.Reporte;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {
}