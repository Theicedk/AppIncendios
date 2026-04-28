package cl.duoc.valledelsol.ms_alarmas.dto;

import cl.duoc.valledelsol.ms_alarmas.enums.Severidad;

public record ReporteIncendioKafkaDTO(
    String encabezado,
    String descripcion,
    Double latitud,
    Double longitud,
    Boolean verificado,
    String estadoIncendio,
    Long reporteId,
    Severidad severidad
) {
}