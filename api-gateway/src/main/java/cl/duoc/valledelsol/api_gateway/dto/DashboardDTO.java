package cl.duoc.valledelsol.api_gateway.dto;

import java.util.List;

public record DashboardDTO(
    List<ReporteDTO> reportes,
    List<FocoDTO> focos
) {}
