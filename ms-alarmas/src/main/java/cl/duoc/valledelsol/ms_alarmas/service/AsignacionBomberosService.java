package cl.duoc.valledelsol.ms_alarmas.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.valledelsol.ms_alarmas.entity.CompaniaBomberos;
import cl.duoc.valledelsol.ms_alarmas.repository.CompaniaBomberosRepository;

@Service
public class AsignacionBomberosService {

    private static final double RADIO_TIERRA_KM = 6371.0;

    private final CompaniaBomberosRepository companiaBomberosRepository;

    public AsignacionBomberosService(CompaniaBomberosRepository companiaBomberosRepository) {
        this.companiaBomberosRepository = companiaBomberosRepository;
    }

    public CompaniaBomberos seleccionarMasCercana(Double lat, Double lon) {
        List<CompaniaBomberos> activas = companiaBomberosRepository.findByActivaTrue();

        if (activas.isEmpty()) {
            throw new IllegalStateException("No hay compañías activas disponibles");
        }

        return activas.stream()
            .filter(compania -> compania.getLatitud() != null && compania.getLongitud() != null)
            .min(Comparator.comparingDouble(compania -> calcularDistanciaKm(lat, lon, compania.getLatitud(), compania.getLongitud())))
            .orElseThrow(() -> new IllegalStateException("No hay compañías activas disponibles"));
    }

    private double calcularDistanciaKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RADIO_TIERRA_KM * c;
    }
}
