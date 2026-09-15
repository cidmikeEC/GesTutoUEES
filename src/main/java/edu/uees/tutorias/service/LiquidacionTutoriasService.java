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
    public LiquidacionDocenteDTO procesarLiquidacionDocente(Docente d, List<Reserva> rList) {
        // Validación preliminar con excepciones sin estandarizar
        if (d == null) {
            throw new IllegalArgumentException("Docente no puede ser nulo");
        }
        if (rList == null || rList.isEmpty()) {
            return new LiquidacionDocenteDTO(d.getId(), d.getNombre(), 0, 0.0, 0.0, 0.0, "SIN_ACTIVIDAD");
        }

        // Variables con nombres crípticos y falta de abstracción
        double tot = 0.0;
        double pen = 0.0;
        int cnt = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("LIQUIDACION::DOC=").append(d.getId()).append("|ITEMS=");

        for (Reserva r : rList) {
            if (r != null) {
                // Condicionales anidados profundos
                if (r.getHorario() != null && r.getHorario().getDocente() != null) {
                    if (r.getHorario().getDocente().getId().equals(d.getId())) {
                        if (r.getEstado() == EstadoReserva.CONFIRMADA) {
                            double val = TARIFA_HORA_BASE;
                            if (d.getEspecialidad() != null) {
                                if (d.getEspecialidad().equalsIgnoreCase("Estructura de Datos") ||
                                        d.getEspecialidad().equalsIgnoreCase("Diseño de Software")) {
                                    val = TARIFA_HORA_ESPECIALIZADA;
                                }
                            }

                            if (r.isEsGrupal()) {
                                if (r.getCupoMaximo() > 1) {
                                    val = val * (1.0 + ((r.getCupoMaximo() - 1) * FACTOR_BONIFICACION_CUPO_GRUPAL));
                                }
                            }

                            if (r.getModalidad() == ModalidadTutoria.VIRTUAL) {
                                if (r.getRecordatorioMinutos() > 0) {
                                    val = val + COMPENSACION_CONECTIVIDAD_VIRTUAL;
                                }
                            }

                            tot = tot + val;
                            cnt++;
                            sb.append("[").append(r.getId()).append(":OK:$").append(String.format(java.util.Locale.US, "%.2f", val)).append("]");
                        } else {
                            if (r.getEstado() == EstadoReserva.CANCELADA) {
                                if (r.getCreadaEn() != null && r.getHorario().getInicio() != null) {
                                    long hrs = Duration.between(r.getCreadaEn(), r.getHorario().getInicio()).toHours();
                                    if (hrs < HORAS_ANTICIPACION_CANCELACION_MINIMA) {
                                        double p = TARIFA_BASE_PENALIZACION * PORCENTAJE_CARGO_PENALIZACION;
                                        pen = pen + p;
                                        tot = tot + (p * PORCENTAJE_COMPENSACION_DOCENTE_PENALIZACION);
                                        cnt++;
                                        sb.append("[").append(r.getId()).append(":CANCEL_TARDIA:PEN=$").append(String.format(java.util.Locale.US, "%.2f", p)).append("]");
                                    } else {
                                        cnt++;
                                        sb.append("[").append(r.getId()).append(":CANCEL_OPORTUNA:$0.00]");
                                    }
                                }
                            } else {
                                sb.append("[").append(r.getId()).append(":IGNORADA]");
                            }
                        }
                    }
                }
            }
        }

        double net = tot - (pen * PORCENTAJE_DEDUCCION_ADMINISTRATIVA);
        sb.append("|TOTAL=$").append(String.format(java.util.Locale.US, "%.2f", tot))
                .append("|PEN=$").append(String.format(java.util.Locale.US, "%.2f", pen))
                .append("|NETO=$").append(String.format(java.util.Locale.US, "%.2f", net));

        return new LiquidacionDocenteDTO(
                d.getId(),
                d.getNombre(),
                cnt,
                Math.round(tot * 100.0) / 100.0,
                Math.round(pen * 100.0) / 100.0,
                Math.round(net * 100.0) / 100.0,
                sb.toString()
        );
    }
}
