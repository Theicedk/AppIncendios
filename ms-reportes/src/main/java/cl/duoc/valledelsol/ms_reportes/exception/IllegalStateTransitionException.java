package cl.duoc.valledelsol.ms_reportes.exception;

import cl.duoc.valledelsol.ms_reportes.enums.EstadoIncendio;

public class IllegalStateTransitionException extends RuntimeException {

    public IllegalStateTransitionException(EstadoIncendio from, EstadoIncendio to) {
        super("Transición ilegal de estado: " + from + " -> " + to);
    }
}
