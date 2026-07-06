package cl.duoc.valledelsol.ms_geolocalizacion.dto;

public record ZonaRiesgoDTO(
    Long id,
    String descripcion,
    Double latitud,
    Double longitud
) {}
