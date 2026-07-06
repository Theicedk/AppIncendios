package cl.duoc.valledelsol.ms_alarmas.dto;

public record CompaniaRequestDTO(
    String nombre,
    double lat,
    double lng,
    boolean activa
) {}
