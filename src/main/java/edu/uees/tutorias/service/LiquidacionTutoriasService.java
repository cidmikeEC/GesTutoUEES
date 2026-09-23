package edu.uees.tutorias.service;

import edu.uees.tutorias.domain.Dinero;
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

    private final CalculadorHonorariosDocente calculadorHonorarios;
    private final PoliticaCancelacion politicaCancelacion;

    public LiquidacionTutoriasService() {
        this(new CalculadorHonorariosDocente(), new PoliticaCancelacion());
    }

    public LiquidacionTutoriasService(CalculadorHonorariosDocente calculadorHonorarios) {
        this(calculadorHonorarios, new PoliticaCancelacion());
    }

    public LiquidacionTutoriasService(CalculadorHonorariosDocente calculadorHonorarios, PoliticaCancelacion politicaCancelacion) {
        this.calculadorHonorarios = calculadorHonorarios != null ? calculadorHonorarios : new CalculadorHonorariosDocente();
        this.politicaCancelacion = politicaCancelacion != null ? politicaCancelacion : new PoliticaCancelacion();
    }

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

        Dinero totalHonorarios = Dinero.cero();
        Dinero totalPenalizaciones = Dinero.cero();
        int totalTutoriasProcesadas = 0;
        StringBuilder detalleBitacora = new StringBuilder();
        detalleBitacora.append("LIQUIDACION::DOC=").append(docente.getId()).append("|ITEMS=");

        for (Reserva reserva : reservas) {
            if (!perteneceADocente(reserva, docente)) {
                continue;
            }

            if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
                Dinero honorarioReserva = calculadorHonorarios.calcularHonorarioConfirmada(reserva, docente);
                totalHonorarios = totalHonorarios.sumar(honorarioReserva);
                totalTutoriasProcesadas++;
                detalleBitacora.append("[").append(reserva.getId()).append(":OK:")
                        .append(honorarioReserva.toString()).append("]");

            } else if (reserva.getEstado() == EstadoReserva.CANCELADA) {
                if (politicaCancelacion.esCancelacionTardia(reserva)) {
                    Dinero montoPenalizacion = politicaCancelacion.calcularMontoPenalizacion();
                    Dinero compensacionDocente = politicaCancelacion.calcularCompensacionDocente(montoPenalizacion);
                    totalPenalizaciones = totalPenalizaciones.sumar(montoPenalizacion);
                    totalHonorarios = totalHonorarios.sumar(compensacionDocente);
                    totalTutoriasProcesadas++;
                    detalleBitacora.append("[").append(reserva.getId()).append(":CANCEL_TARDIA:PEN=")
                            .append(montoPenalizacion.toString()).append("]");
                } else {
                    totalTutoriasProcesadas++;
                    detalleBitacora.append("[").append(reserva.getId()).append(":CANCEL_OPORTUNA:$0.00]");
                }
            } else {
                detalleBitacora.append("[").append(reserva.getId()).append(":IGNORADA]");
            }
        }

        Dinero deduccionAdministrativa = politicaCancelacion.calcularDeduccionAdministrativa(totalPenalizaciones);
        Dinero montoNeto = totalHonorarios.restar(deduccionAdministrativa);

        detalleBitacora.append("|TOTAL=").append(totalHonorarios.toString())
                .append("|PEN=").append(totalPenalizaciones.toString())
                .append("|NETO=").append(montoNeto.toString());

        return new LiquidacionDocenteDTO(
                docente.getId(),
                docente.getNombre(),
                totalTutoriasProcesadas,
                totalHonorarios.getValor(),
                totalPenalizaciones.getValor(),
                montoNeto.getValor(),
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
}
