package cl.duoc.valledelsol.ms_geolocalizacion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReporteIncendioKafkaDTO(
        Long reporteId,
        Double latitud,
        Double longitud
) {
}