package cl.duoc.valledelsol.ms_geolocalizacion.entity;

import org.locationtech.jts.geom.Point;

import cl.duoc.valledelsol.ms_geolocalizacion.enums.TipoRecurso;
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
@Table(name = "RecursoHidrico") //Nombre de la tabla en la base de datos
@Data //Creador automatico de getters y setters
@AllArgsConstructor //Crea un constructor con todo los campos
@NoArgsConstructor //Crea un constructor vacio para poder crear objetos
public class RecursoHidrico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private TipoRecurso tipo;

    @Column(nullable = false, columnDefinition = "geometry(Point, 4326)")
    private Point ubicacion;

    @Column(nullable = false)
    private String nombre;
}
