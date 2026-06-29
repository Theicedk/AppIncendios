package cl.duoc.valledelsol.ms_reportes.enums;

public enum EstadoIncendio {
    REPORTADO,
    EN_CORROBORACION,
    VERIFICADO,
    ATENDIDO,
    ACTIVO;

    public static EstadoIncendio fromLegacyValue(String value) {
        if (value == null) {
            return null;
        }

        return switch (value.trim().toUpperCase()) {
            case "ACTIVO", "REPORTADO" -> REPORTADO;
            case "EN_CORROBORACION" -> EN_CORROBORACION;
            case "VERIFICADO" -> VERIFICADO;
            case "ATENDIDO" -> ATENDIDO;
            default -> valueOf(value.trim().toUpperCase());
        };
    }

    public boolean puedeTransicionarA(EstadoIncendio destino) {
        if (destino == null) {
            return false;
        }

        return switch (this) {
            case REPORTADO, ACTIVO -> destino == EN_CORROBORACION || destino == VERIFICADO;
            case EN_CORROBORACION -> destino == VERIFICADO;
            case VERIFICADO -> destino == ATENDIDO;
            case ATENDIDO -> false;
        };
    }
}
