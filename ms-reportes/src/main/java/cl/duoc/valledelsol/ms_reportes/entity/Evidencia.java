package cl.duoc.valledelsol.ms_reportes.entity;

import cl.duoc.valledelsol.ms_reportes.enums.TipoEvidencia;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Evidencia") //Nombre de la tabla en la base de datos
@Data //Creador automatico de getters y setters
@AllArgsConstructor //Crea un constructor con todo los campos
@NoArgsConstructor //Crea un constructor vacio para poder crear objetos
public class Evidencia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING) // Usa STRING para guardar el nombre, u ORDINAL para el índice
    @Column(name = "tipo_evidencia")
    private TipoEvidencia evidencia;

    @ManyToOne
    @JoinColumn(name = "reporte_id", nullable = false)
    private Reporte reporteId;
}
