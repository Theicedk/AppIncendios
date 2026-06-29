package cl.duoc.valledelsol.ms_alarmas.exception;

public class IncidenteNoVerificadoException extends RuntimeException {

    public IncidenteNoVerificadoException(Long id) {
        super("El reporte " + id + " no está verificado.");
    }
}
