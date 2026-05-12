package cl.duoc.valledelsol.api_gateway.dto;

public record ReporteDTO(
    Long id,
    String descripcion,
    Double latitud,
    Double longitud,
    String estado
) {}
