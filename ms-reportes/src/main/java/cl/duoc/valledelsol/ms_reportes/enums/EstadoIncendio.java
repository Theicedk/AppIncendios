package cl.duoc.valledelsol.ms_reportes.enums;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class EstadoIncendio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadosIncendio estado;

    public enum EstadosIncendio {
        REPORTADO,
        ACTIVO,
        CONTROLADO,
        EXTINGUIDO
    }
}
