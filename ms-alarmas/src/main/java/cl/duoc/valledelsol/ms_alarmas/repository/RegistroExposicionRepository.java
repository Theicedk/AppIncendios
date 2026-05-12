package cl.duoc.valledelsol.ms_alarmas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.valledelsol.ms_alarmas.entity.RegistroExposicion;

@Repository
public interface RegistroExposicionRepository extends JpaRepository<RegistroExposicion, Long> {
}
