package cl.duoc.valledelsol.ms_reportes.dto;

public record ReporteKafkaEvent(
    Long id,
    String descripcion,
    Double latitud,
    Double longitud
) {}
