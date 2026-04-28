package cl.duoc.valledelsol.ms_alarmas.entity;

import org.springframework.data.geo.Point;

import cl.duoc.valledelsol.ms_reportes.entity.Reporte;
import cl.duoc.valledelsol.ms_alarmas.enums.Severidad;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "Alarma") //Nombre de la tabla en la base de datos
@Data //Creador automatico de getters y setters
@AllArgsConstructor //Crea un constructor con todo los campos
@NoArgsConstructor //Crea un constructor vacio para poder crear objetos
public class Alarma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlarma;

    @Column(nullable = false)
    private String mensaje;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Severidad severidad;

    @ManyToOne
    @JoinColumn(name = "idReporte", nullable = false)
    private Reporte reporteId;

    @Transient //Atributo que sirve para informar que la base de datos que este sera un dato no persistente
    private Point ubicacion;

    @Column(nullable = false)
    private Double radioKm;

}
