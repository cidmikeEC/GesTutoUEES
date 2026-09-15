package edu.uees.tutorias.service;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.EstadoReserva;
import edu.uees.tutorias.domain.ModalidadTutoria;
import edu.uees.tutorias.domain.Reserva;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de liquidación financiera y nómina docente de tutorías (ESTADO REFACTORIZADO - DESPUÉS).
 *
 * Kata de Refactorización Ae4 - Evidencia de diseño limpio:
 * 1. Métodos pequeños y con Responsabilidad Única (SRP): Se extrajeron cálculos de honorarios,
 *    verificación de propiedad y evaluación de cancelaciones.
 * 2. Constantes Simbólicas: Se erradicaron todos los Magic Numbers asociados a tarifas y porcentajes.
 * 3. Nombres con Intención de Dominio: Se eliminaron abreviaturas crípticas en favor de términos ubicuos.
 * 4. Flujo Aplanado (Guard Clauses): Se reemplazó el anidamiento profundo con retornos y saltos tempranos.
 * 5. Cohesión y Reducción de Feature Envy: Lógica encapsulada y legible.
 *
 * Se garantiza 100% de preservación de comportamiento observable verificado con suite de pruebas JUnit 5.
 */
public class LiquidacionTutoriasService {

    // Constantes simbólicas para erradicar Magic Numbers (Refactorización 1)
    private static final double TARIFA_HORA_BASE = 25.0;
    private static final double TARIFA_HORA_ESPECIALIZADA = 30.0;
    private static final double COMPENSACION_CONECTIVIDAD_VIRTUAL = 2.50;
    private static final double FACTOR_BONIFICACION_CUPO_GRUPAL = 0.15;
    private static final long HORAS_ANTICIPACION_CANCELACION_MINIMA = 24;
    private static final double TARIFA_BASE_PENALIZACION = 15.0;
    private static final double PORCENTAJE_CARGO_PENALIZACION = 0.50;
    private static final double PORCENTAJE_COMPENSACION_DOCENTE_PENALIZACION = 0.50;
    private static final double PORCENTAJE_DEDUCCION_ADMINISTRATIVA = 0.10;

    /**
     * Procesa la liquidación económica de las tutorías de un docente en un periodo.
     */
    public LiquidacionDocenteDTO procesarLiquidacionDocente(Docente docente, List<Reserva> reservas) {
        if (docente == null) {
            throw new IllegalArgumentException("Docente no puede ser nulo");
        }
        if (reservas == null || reservas.isEmpty()) {
            return new LiquidacionDocenteDTO(docente.getId(), docente.getNombre(), 0, 0.0, 0.0, 0.0, "SIN_ACTIVIDAD");
        }

        double totalHonorarios = 0.0;
        double totalPenalizaciones = 0.0;
        int totalTutoriasProcesadas = 0;
        StringBuilder detalleBitacora = new StringBuilder();
        detalleBitacora.append("LIQUIDACION::DOC=").append(docente.getId()).append("|ITEMS=");

        for (Reserva reserva : reservas) {
            if (!perteneceADocente(reserva, docente)) {
                continue;
            }

            if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
                double honorarioReserva = calcularHonorarioConfirmada(reserva, docente);
                totalHonorarios += honorarioReserva;
                totalTutoriasProcesadas++;
                detalleBitacora.append("[").append(reserva.getId()).append(":OK:$")
                        .append(String.format(java.util.Locale.US, "%.2f", honorarioReserva)).append("]");

            } else if (reserva.getEstado() == EstadoReserva.CANCELADA) {
                if (esCancelacionTardia(reserva)) {
                    double montoPenalizacion = calcularMontoPenalizacion();
                    totalPenalizaciones += montoPenalizacion;
                    totalHonorarios += (montoPenalizacion * PORCENTAJE_COMPENSACION_DOCENTE_PENALIZACION);
                    totalTutoriasProcesadas++;
                    detalleBitacora.append("[").append(reserva.getId()).append(":CANCEL_TARDIA:PEN=$")
                            .append(String.format(java.util.Locale.US, "%.2f", montoPenalizacion)).append("]");
                } else {
                    totalTutoriasProcesadas++;
                    detalleBitacora.append("[").append(reserva.getId()).append(":CANCEL_OPORTUNA:$0.00]");
                }
            } else {
                detalleBitacora.append("[").append(reserva.getId()).append(":IGNORADA]");
            }
        }

        double montoNeto = totalHonorarios - (totalPenalizaciones * PORCENTAJE_DEDUCCION_ADMINISTRATIVA);
        detalleBitacora.append("|TOTAL=$").append(String.format(java.util.Locale.US, "%.2f", totalHonorarios))
                .append("|PEN=$").append(String.format(java.util.Locale.US, "%.2f", totalPenalizaciones))
                .append("|NETO=$").append(String.format(java.util.Locale.US, "%.2f", montoNeto));

        return new LiquidacionDocenteDTO(
                docente.getId(),
                docente.getNombre(),
                totalTutoriasProcesadas,
                Math.round(totalHonorarios * 100.0) / 100.0,
                Math.round(totalPenalizaciones * 100.0) / 100.0,
                Math.round(montoNeto * 100.0) / 100.0,
                detalleBitacora.toString()
        );
    }

    /**
     * Determina si la reserva pertenece al docente indicado evitando NullPointerException.
     */
    private boolean perteneceADocente(Reserva reserva, Docente docente) {
        if (reserva == null || reserva.getHorario() == null || reserva.getHorario().getDocente() == null) {
            return false;
        }
        return reserva.getHorario().getDocente().getId().equals(docente.getId());
    }

    /**
     * Determina la tarifa horaria base según el perfil y especialidad del docente.
     */
    private double obtenerTarifaBase(Docente docente) {
        if (docente != null && docente.getEspecialidad() != null) {
            if (docente.getEspecialidad().equalsIgnoreCase("Estructura de Datos") ||
                    docente.getEspecialidad().equalsIgnoreCase("Diseño de Software")) {
                return TARIFA_HORA_ESPECIALIZADA;
            }
        }
        return TARIFA_HORA_BASE;
    }

    /**
     * Calcula el honorario bruto devengado por una tutoría confirmada.
     */
    private double calcularHonorarioConfirmada(Reserva reserva, Docente docente) {
        double honorario = obtenerTarifaBase(docente);

        if (reserva.isEsGrupal() && reserva.getCupoMaximo() > 1) {
            honorario = honorario * (1.0 + ((reserva.getCupoMaximo() - 1) * FACTOR_BONIFICACION_CUPO_GRUPAL));
        }

        if (reserva.getModalidad() == ModalidadTutoria.VIRTUAL && reserva.getRecordatorioMinutos() > 0) {
            honorario = honorario + COMPENSACION_CONECTIVIDAD_VIRTUAL;
        }

        return honorario;
    }

    /**
     * Evalúa si una cancelación se efectuó fuera del margen mínimo de cortesía (cancelación tardía).
     */
    private boolean esCancelacionTardia(Reserva reserva) {
        if (reserva.getCreadaEn() == null || reserva.getHorario() == null || reserva.getHorario().getInicio() == null) {
            return false;
        }
        long horasAnticipacion = Duration.between(reserva.getCreadaEn(), reserva.getHorario().getInicio()).toHours();
        return horasAnticipacion < HORAS_ANTICIPACION_CANCELACION_MINIMA;
    }

    /**
     * Calcula el monto de penalización económica por cancelación tardía.
     */
    private double calcularMontoPenalizacion() {
        return TARIFA_BASE_PENALIZACION * PORCENTAJE_CARGO_PENALIZACION;
    }
}
