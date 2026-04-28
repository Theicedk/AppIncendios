package cl.duoc.valledelsol.ms_alarmas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.valledelsol.ms_alarmas.entity.Alarma;

public interface AlarmaRepository extends JpaRepository<Alarma, Long> {
}