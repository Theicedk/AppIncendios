package cl.duoc.valledelsol.ms_geolocalizacion.dto;

public record ZonaRiesgoRequestDTO(
    String descripcion,
    Double latitud,
    Double longitud
) {}
