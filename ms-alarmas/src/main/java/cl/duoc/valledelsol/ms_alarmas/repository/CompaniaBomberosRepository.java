package cl.duoc.valledelsol.ms_alarmas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.valledelsol.ms_alarmas.entity.CompaniaBomberos;

public interface CompaniaBomberosRepository extends JpaRepository<CompaniaBomberos, Long> {

    List<CompaniaBomberos> findByActivaTrue();
}
