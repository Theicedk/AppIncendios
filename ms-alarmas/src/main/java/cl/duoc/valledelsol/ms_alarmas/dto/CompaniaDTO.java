package cl.duoc.valledelsol.ms_alarmas.dto;

public record CompaniaDTO(
    Long id,
    String nombre,
    double lat,
    double lng,
    boolean activa
) {}
