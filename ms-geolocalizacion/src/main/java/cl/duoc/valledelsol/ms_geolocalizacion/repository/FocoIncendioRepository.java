package cl.duoc.valledelsol.ms_geolocalizacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.valledelsol.ms_geolocalizacion.entity.FocoIncendio;

public interface FocoIncendioRepository extends JpaRepository<FocoIncendio, Long> {
}