package edu.uees.tutorias.service;

import edu.uees.tutorias.domain.Dinero;
import edu.uees.tutorias.domain.Reserva;
import java.time.Duration;

/**
 * Clase extraída que encapsula las reglas de negocio y políticas financieras para cancelaciones de tutorías.
 *
 * Refactorización 3 (Ae5): Extract Class & Move Method.
 * Reorganiza responsabilidades de penalización y evaluación de tiempos de cortesía,
 * liberando a LiquidacionTutoriasService para que actúe exclusivamente como orquestador.
 */
public class PoliticaCancelacion {

    public static final long HORAS_ANTICIPACION_MINIMA = 24;
    public static final Dinero TARIFA_BASE_PENALIZACION = Dinero.de(15.0);
    public static final double FACTOR_CARGO_PENALIZACION = 0.50;
    public static final double FACTOR_COMPENSACION_DOCENTE = 0.50;
    public static final double FACTOR_DEDUCCION_ADMINISTRATIVA = 0.10;

    /**
     * Evalúa si una cancelación se efectuó fuera del margen mínimo reglamentario (cancelación tardía).
     */
    public boolean esCancelacionTardia(Reserva reserva) {
        if (reserva == null) {
            return false;
        }
        if (reserva.getCreadaEn() == null || reserva.getHorario() == null || reserva.getHorario().getInicio() == null) {
            return false;
        }
        long horasAnticipacion = Duration.between(reserva.getCreadaEn(), reserva.getHorario().getInicio()).toHours();
        return horasAnticipacion < HORAS_ANTICIPACION_MINIMA;
    }

    /**
     * Calcula el monto total de penalización económica por cancelación tardía.
     */
    public Dinero calcularMontoPenalizacion() {
        return TARIFA_BASE_PENALIZACION.multiplicarPor(FACTOR_CARGO_PENALIZACION);
    }

    /**
     * Calcula la compensación económica que se acredita al docente afectado.
     */
    public Dinero calcularCompensacionDocente(Dinero montoPenalizacion) {
        if (montoPenalizacion == null) {
            return Dinero.cero();
        }
        return montoPenalizacion.multiplicarPor(FACTOR_COMPENSACION_DOCENTE);
    }

    /**
     * Calcula la deducción institucional administrativa sobre el cargo por penalización.
     */
    public Dinero calcularDeduccionAdministrativa(Dinero totalPenalizaciones) {
        if (totalPenalizaciones == null) {
            return Dinero.cero();
        }
        return totalPenalizaciones.multiplicarPor(FACTOR_DEDUCCION_ADMINISTRATIVA);
    }
}
