package cl.duoc.valledelsol.ms_geolocalizacion.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.duoc.valledelsol.ms_geolocalizacion.entity.FocoIncendio;

public interface FocoIncendioRepository extends JpaRepository<FocoIncendio, Long> {

    @Query(value = """
            select *
            from foco_incendio f
            where ST_DWithin(
                f.ubicacion,
                ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
                :radioMetros
            )
            order by f.fecha_creacion desc
            limit 1
            """, nativeQuery = true)
    Optional<FocoIncendio> buscarCercano(@Param("lat") Double lat,
                                         @Param("lon") Double lon,
                                         @Param("radioMetros") Integer radioMetros);

    @Query(value = """
            select extract(hour from f.fecha_creacion) as hora,
                   extract(dow from f.fecha_creacion) as dow,
                   ST_AsText(ST_Centroid(ST_Collect(f.ubicacion))) as centroide,
                   count(*) as total
            from foco_incendio f
            where f.fecha_creacion >= :desde
            group by extract(hour from f.fecha_creacion), extract(dow from f.fecha_creacion)
            order by extract(hour from f.fecha_creacion), extract(dow from f.fecha_creacion)
            """, nativeQuery = true)
    List<Object[]> heatmapAgregado(@Param("desde") LocalDateTime desde);
}