package cl.duoc.valledelsol.api_gateway.dto;

public record FocoDTO(
    Long id,
    Double latitud,
    Double longitud,
    String estado
) {}
