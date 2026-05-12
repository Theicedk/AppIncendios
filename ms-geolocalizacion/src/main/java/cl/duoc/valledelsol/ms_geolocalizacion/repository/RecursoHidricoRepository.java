package cl.duoc.valledelsol.ms_geolocalizacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.valledelsol.ms_geolocalizacion.entity.RecursoHidrico;

@Repository
public interface RecursoHidricoRepository extends JpaRepository<RecursoHidrico, Long> {
}
