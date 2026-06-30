package cl.duoc.valledelsol.ms_geolocalizacion.dto;

public record HeatmapDTO(
    Integer hora,
    Integer diaSemana,
    String centroide,
    Long total
) {
}
