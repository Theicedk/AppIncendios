package cl.duoc.valledelsol.ms_alarmas.dto;

public record ReporteKafkaEvent(
    Long id,
    String descripcion,
    Double latitud,
    Double longitud
) {}
