package cl.duoc.valledelsol.ms_geolocalizacion.entity;

import org.locationtech.jts.geom.Point;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "foco_incendio")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FocoIncendio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reporte_id")
    private Long reporteId;

    @Column(nullable = false, columnDefinition = "geometry(Point, 4326)")
    private Point ubicacion;

}
