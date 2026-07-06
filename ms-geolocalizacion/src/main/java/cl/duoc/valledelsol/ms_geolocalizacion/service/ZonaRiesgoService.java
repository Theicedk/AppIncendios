package cl.duoc.valledelsol.ms_geolocalizacion.service;

import java.util.List;

import cl.duoc.valledelsol.ms_geolocalizacion.dto.ZonaRiesgoDTO;
import cl.duoc.valledelsol.ms_geolocalizacion.dto.ZonaRiesgoRequestDTO;

public interface ZonaRiesgoService {

    List<ZonaRiesgoDTO> obtenerTodas();

    ZonaRiesgoDTO obtenerPorId(Long id);

    ZonaRiesgoDTO crear(ZonaRiesgoRequestDTO dto);

    ZonaRiesgoDTO actualizar(Long id, ZonaRiesgoRequestDTO dto);

    void eliminar(Long id);
}
