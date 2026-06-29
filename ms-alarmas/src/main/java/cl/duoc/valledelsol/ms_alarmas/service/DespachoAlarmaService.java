package cl.duoc.valledelsol.ms_alarmas.service;

import java.time.LocalDateTime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.valledelsol.ms_alarmas.dto.AlertaWebSocketDTO;
import cl.duoc.valledelsol.ms_alarmas.entity.Alarma;
import cl.duoc.valledelsol.ms_alarmas.entity.CompaniaBomberos;
import cl.duoc.valledelsol.ms_alarmas.enums.EstadoAlarma;
import cl.duoc.valledelsol.ms_alarmas.enums.Severidad;
import cl.duoc.valledelsol.ms_alarmas.exception.IncidenteNoVerificadoException;
import cl.duoc.valledelsol.ms_alarmas.repository.AlarmaRepository;

@Service
public class DespachoAlarmaService {

    private final AlarmaRepository alarmaRepository;
    private final AsignacionBomberosService asignacionBomberosService;
    private final SimpMessagingTemplate messagingTemplate;

    public DespachoAlarmaService(AlarmaRepository alarmaRepository,
                                 AsignacionBomberosService asignacionBomberosService,
                                 SimpMessagingTemplate messagingTemplate) {
        this.alarmaRepository = alarmaRepository;
        this.asignacionBomberosService = asignacionBomberosService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public Alarma disparar(Long reporteId, Double lat, Double lon, boolean verificado) {
        if (!verificado) {
            throw new IncidenteNoVerificadoException(reporteId);
        }

        CompaniaBomberos compania = asignacionBomberosService.seleccionarMasCercana(lat, lon);

        Alarma alarma = new Alarma();
        alarma.setMensaje("Alerta despachada para reporte " + reporteId);
        alarma.setReporteId(reporteId);
        alarma.setCompaniaAsignadaId(compania.getId());
        alarma.setSeveridad(Severidad.ROJA);
        alarma.setEstado(EstadoAlarma.PENDIENTE);
        alarma.setCreadaEn(LocalDateTime.now());

        Alarma guardada = alarmaRepository.save(alarma);

        AlertaWebSocketDTO alerta = new AlertaWebSocketDTO(
            guardada.getId(),
            guardada.getReporteId(),
            lat,
            lon,
            "ALERTA_NUEVA"
        );

        messagingTemplate.convertAndSend("/topic/compania/" + compania.getId(), alerta);

        return guardada;
    }

    @Transactional
    public Alarma aceptar(Long alarmaId) {
        Alarma alarma = alarmaRepository.findById(alarmaId)
            .orElseThrow(() -> new IllegalArgumentException("Alarma no encontrada: " + alarmaId));

        if (alarma.getEstado() != EstadoAlarma.PENDIENTE) {
            throw new IllegalStateException("La alarma no se encuentra pendiente");
        }

        alarma.setEstado(EstadoAlarma.ACEPTADA);
        alarma.setAceptadaEn(LocalDateTime.now());
        return alarmaRepository.save(alarma);
    }
}
