package cl.duoc.valledelsol.ms_reportes.enums;

public enum EstadoIncendio {
    REPORTADO,
    EN_CORROBORACION,
    VERIFICADO,
    ATENDIDO;

    public boolean puedeTransicionarA(EstadoIncendio destino) {
        if (destino == null) {
            return false;
        }

        return switch (this) {
            case REPORTADO -> destino == EN_CORROBORACION;
            case EN_CORROBORACION -> destino == VERIFICADO;
            case VERIFICADO -> destino == ATENDIDO;
            case ATENDIDO -> false;
        };
    }
}
