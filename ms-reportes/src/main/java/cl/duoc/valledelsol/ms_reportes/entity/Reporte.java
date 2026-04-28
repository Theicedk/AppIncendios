package cl.duoc.valledelsol.ms_reportes.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.data.geo.Point;

import cl.duoc.valledelsol.ms_reportes.enums.EstadoIncendio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Reporte")
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @Transient //Atributo que sirve para informar que la base de datos que este sera un dato no persistente
    private Point ubicacion;

    @Column(nullable = false)
    private boolean verificado;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoIncendio estadoIncendio;
}