package cl.duoc.valledelsol.ms_reportes.entity;
import java.time.LocalDateTime;

import org.springframework.data.geo.Point;
import cl.duoc.valledelsol.ms_reportes.enums.EstadoIncendio;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;


@Entity
@Table(name = "reportes") //Nombre de la tabla en la base de datos
@Data //Creador automatico de getters y setters
@AllArgsConstructor //Crea un constructor con todo los campos
@NoArgsConstructor //Crea un constructor vacio para poder crear objetos
public class Reporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private String encabezado;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Point ubicacion;

    @Column(nullable = false)
    private boolean verificado;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // Encargado de almacenar como string los estados en la base de datos
    private EstadoIncendio estadoIncendio;

    


}