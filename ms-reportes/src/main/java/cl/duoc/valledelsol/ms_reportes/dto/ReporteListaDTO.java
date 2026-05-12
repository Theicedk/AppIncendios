package cl.duoc.valledelsol.ms_reportes.dto;

public record ReporteListaDTO(
    Long id,
    String descripcion,
    Double latitud,
    Double longitud,
    String estado
) {
}
