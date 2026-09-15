package edu.uees.tutorias.service;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.EstadoReserva;
import edu.uees.tutorias.domain.ModalidadTutoria;
import edu.uees.tutorias.domain.Reserva;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio legado de liquidación financiera y nómina docente de tutorías (ESTADO INICIAL - ANTES).
 *
 * NOTA DE DEUDA TÉCNICA (Kata de Refactorización Ae4):
 * Este componente fue desarrollado inicialmente como un script procedural monolítico.
 * Presenta múltiples Code Smells identificados:
 * 1. Long Method: Un único método extenso asumiendo cálculo, validación, penalización y formateo.
 * 2. Magic Numbers: Tarifas, factores multiplicadores y umbrales de horas quemados en el código.
 * 3. Poor Naming & Primitive Obsession: Variables crípticas de una letra o abreviaturas opacas.
 * 4. Nested Conditionals (Arrow Anti-Pattern): Múltiples niveles de if-else anidados.
 * 5. Feature Envy: El método extrae exhaustivamente datos internos de Reserva y Horario.
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
        // Validación preliminar con excepciones sin estandarizar
        if (docente == null) {
            throw new IllegalArgumentException("Docente no puede ser nulo");
        }
        if (reservas == null || reservas.isEmpty()) {
            return new LiquidacionDocenteDTO(docente.getId(), docente.getNombre(), 0, 0.0, 0.0, 0.0, "SIN_ACTIVIDAD");
        }

        // Variables descriptivas con intención de dominio clara
        double totalHonorarios = 0.0;
        double totalPenalizaciones = 0.0;
        int totalTutoriasProcesadas = 0;
        StringBuilder detalleBitacora = new StringBuilder();
        detalleBitacora.append("LIQUIDACION::DOC=").append(docente.getId()).append("|ITEMS=");

        for (Reserva reserva : reservas) {
            // Cláusulas de guarda tempranas para aplanar la pirámide de anidación
            if (reserva == null) {
                continue;
            }
            if (reserva.getHorario() == null || reserva.getHorario().getDocente() == null) {
                continue;
            }
            if (!reserva.getHorario().getDocente().getId().equals(docente.getId())) {
                continue;
            }

            if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
                double honorarioReserva = TARIFA_HORA_BASE;
                if (docente.getEspecialidad() != null) {
                    if (docente.getEspecialidad().equalsIgnoreCase("Estructura de Datos") ||
                            docente.getEspecialidad().equalsIgnoreCase("Diseño de Software")) {
                        honorarioReserva = TARIFA_HORA_ESPECIALIZADA;
                    }
                }

                if (reserva.isEsGrupal() && reserva.getCupoMaximo() > 1) {
                    honorarioReserva = honorarioReserva * (1.0 + ((reserva.getCupoMaximo() - 1) * FACTOR_BONIFICACION_CUPO_GRUPAL));
                }

                if (reserva.getModalidad() == ModalidadTutoria.VIRTUAL && reserva.getRecordatorioMinutos() > 0) {
                    honorarioReserva = honorarioReserva + COMPENSACION_CONECTIVIDAD_VIRTUAL;
                }

                totalHonorarios = totalHonorarios + honorarioReserva;
                totalTutoriasProcesadas++;
                detalleBitacora.append("[").append(reserva.getId()).append(":OK:$").append(String.format(java.util.Locale.US, "%.2f", honorarioReserva)).append("]");
            } else if (reserva.getEstado() == EstadoReserva.CANCELADA) {
                if (reserva.getCreadaEn() != null && reserva.getHorario().getInicio() != null) {
                    long horasAnticipacion = Duration.between(reserva.getCreadaEn(), reserva.getHorario().getInicio()).toHours();
                    if (horasAnticipacion < HORAS_ANTICIPACION_CANCELACION_MINIMA) {
                        double montoPenalizacion = TARIFA_BASE_PENALIZACION * PORCENTAJE_CARGO_PENALIZACION;
                        totalPenalizaciones = totalPenalizaciones + montoPenalizacion;
                        totalHonorarios = totalHonorarios + (montoPenalizacion * PORCENTAJE_COMPENSACION_DOCENTE_PENALIZACION);
                        totalTutoriasProcesadas++;
                        detalleBitacora.append("[").append(reserva.getId()).append(":CANCEL_TARDIA:PEN=$").append(String.format(java.util.Locale.US, "%.2f", montoPenalizacion)).append("]");
                    } else {
                        totalTutoriasProcesadas++;
                        detalleBitacora.append("[").append(reserva.getId()).append(":CANCEL_OPORTUNA:$0.00]");
                    }
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
}
