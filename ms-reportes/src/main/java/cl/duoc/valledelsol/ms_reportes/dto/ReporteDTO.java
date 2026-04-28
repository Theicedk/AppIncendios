package cl.duoc.valledelsol.ms_reportes.dto;

import cl.duoc.valledelsol.ms_reportes.enums.EstadoIncendio;

public record ReporteDTO(
	String encabezado,
	String descripcion,
	Double latitud,
	Double longitud,
	Boolean verificado,
	EstadoIncendio estadoIncendio
) {
}