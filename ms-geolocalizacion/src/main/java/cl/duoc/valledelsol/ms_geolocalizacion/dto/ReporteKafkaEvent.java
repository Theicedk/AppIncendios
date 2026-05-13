package cl.duoc.valledelsol.ms_geolocalizacion.dto;

public record ReporteKafkaEvent(
    Long id,
    String descripcion,
    Double latitud,
    Double longitud
) {}
