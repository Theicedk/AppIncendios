package cl.duoc.valledelsol.ms_alarmas.dto;

public record AlertaWebSocketDTO(
    Long alarmaId,
    Long reporteId,
    Double lat,
    Double lon,
    String tipo
) {
}
