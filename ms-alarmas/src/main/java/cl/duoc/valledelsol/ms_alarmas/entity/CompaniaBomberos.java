package cl.duoc.valledelsol.ms_alarmas.entity;

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
@Table(name = "companias_bomberos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompaniaBomberos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column
    private Double latitud;

    @Column
    private Double longitud;

    @Column(nullable = false)
    private boolean activa = true;
}
