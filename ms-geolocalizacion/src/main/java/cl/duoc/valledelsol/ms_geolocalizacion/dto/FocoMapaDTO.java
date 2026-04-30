package cl.duoc.valledelsol.ms_geolocalizacion.dto;

public record FocoMapaDTO(
    Long id,
    Double latitud,
    Double longitud,
    String estado
) {
}
