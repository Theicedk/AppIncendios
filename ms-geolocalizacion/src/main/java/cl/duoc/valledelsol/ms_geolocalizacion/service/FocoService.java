package cl.duoc.valledelsol.ms_geolocalizacion.service;

import java.util.List;

import java.util.List;

import cl.duoc.valledelsol.ms_geolocalizacion.dto.FocoMapaDTO;
import cl.duoc.valledelsol.ms_geolocalizacion.dto.HeatmapDTO;

public interface FocoService {

    List<FocoMapaDTO> obtenerTodos();

    FocoServiceImpl.ResultadoCluster asignarGrupo(Long reporteId, Double lat, Double lon);

    List<HeatmapDTO> heatmapAgregado();
}