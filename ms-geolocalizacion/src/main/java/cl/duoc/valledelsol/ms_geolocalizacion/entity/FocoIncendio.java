package cl.duoc.valledelsol.ms_geolocalizacion.entity;

import org.springframework.data.geo.Point;

import cl.duoc.valledelsol.ms_geolocalizacion.enums.EstadoFoco;
import cl.duoc.valledelsol.ms_reportes.entity.Reporte;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "FocoIncendio") //Nombre de la tabla en la base de datos
@Data //Creador automatico de getters y setters
@AllArgsConstructor //Crea un constructor con todo los campos
@NoArgsConstructor //Crea un constructor vacio para poder crear objetos
public class FocoIncendio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idReporte", nullable = false)
    private Reporte reporteId;

    @Transient //Atributo que sirve para informar que la base de datos que este sera un dato no persistente
    private Point ubicacion;

    @Column(nullable = false)
    private Double radioEstimado;

    @Column(nullable = false)
    private EstadoFoco estado;

    @Column(nullable = false)
    private Data fechaActualizacion;

}
