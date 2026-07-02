package cl.duoc.valledelsol.ms_reportes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.duoc.valledelsol.ms_reportes.entity.Reporte;
import cl.duoc.valledelsol.ms_reportes.enums.EstadoIncendio;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Reporte r set r.estadoIncendio = :estado where r.grupoId = :grupoId and r.id <> :excluirId")
    int cerrarGrupo(@Param("grupoId") Long grupoId,
                    @Param("estado") EstadoIncendio estado,
                    @Param("excluirId") Long excluirId);

    @Query("SELECT r FROM Reporte r WHERE r.latitud BETWEEN :minLat AND :maxLat AND r.longitud BETWEEN :minLng AND :maxLng")
    List<Reporte> findByBoundingBox(@Param("minLat") double minLat,
                                    @Param("maxLat") double maxLat,
                                    @Param("minLng") double minLng,
                                    @Param("maxLng") double maxLng);
}