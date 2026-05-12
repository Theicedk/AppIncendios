package cl.duoc.valledelsol.ms_alarmas.entity;

import cl.duoc.valledelsol.ms_alarmas.enums.Severidad;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alarmas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alarma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mensaje;

    @Column
    private Long reporteId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Severidad severidad = Severidad.ROJA;

    @jakarta.persistence.OneToMany(mappedBy = "alarma", cascade = jakarta.persistence.CascadeType.ALL, fetch = jakarta.persistence.FetchType.LAZY)
    private java.util.List<RegistroExposicion> exposiciones;
}
