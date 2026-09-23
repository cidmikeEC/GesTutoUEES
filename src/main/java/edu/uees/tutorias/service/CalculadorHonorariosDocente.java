package edu.uees.tutorias.service;

import edu.uees.tutorias.domain.Dinero;
import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.ModalidadTutoria;
import edu.uees.tutorias.domain.Reserva;

/**
 * Clase extraída que encapsula las políticas y cálculos de honorarios para docentes de tutorías.
 *
 * Refactorización 2 (Ae5): Extract Class & Move Method.
 * Resuelve el smell de 'Divergent Change' y violación de SRP en LiquidacionTutoriasService,
 * aislando la lógica de tarifas base, especialidades, bonos grupales y compensación virtual.
 */
public class CalculadorHonorariosDocente {

    public static final Dinero TARIFA_HORA_BASE = Dinero.de(25.0);
    public static final Dinero TARIFA_HORA_ESPECIALIZADA = Dinero.de(30.0);
    public static final Dinero COMPENSACION_CONECTIVIDAD_VIRTUAL = Dinero.de(2.50);
    public static final double FACTOR_BONIFICACION_CUPO_GRUPAL = 0.15;

    /**
     * Determina la tarifa horaria base según el perfil y especialidad del docente.
     */
    public Dinero obtenerTarifaBase(Docente docente) {
        if (docente == null) {
            throw new IllegalArgumentException("Docente no puede ser nulo para determinar tarifa");
        }
        if (docente.getEspecialidad() != null) {
            String esp = docente.getEspecialidad().trim();
            if (esp.equalsIgnoreCase("Estructura de Datos") ||
                    esp.equalsIgnoreCase("Diseño de Software") ||
                    esp.equalsIgnoreCase("Diseno de Software")) {
                return TARIFA_HORA_ESPECIALIZADA;
            }
        }
        return TARIFA_HORA_BASE;
    }

    /**
     * Calcula el honorario bruto devengado por una tutoría confirmada.
     */
    public Dinero calcularHonorarioConfirmada(Reserva reserva, Docente docente) {
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva no puede ser nula");
        }
        if (docente == null) {
            throw new IllegalArgumentException("Docente no puede ser nulo");
        }

        Dinero honorario = obtenerTarifaBase(docente);

        // Bonificación por tutoría grupal (15% por cada cupo adicional a partir del segundo)
        if (reserva.isEsGrupal() && reserva.getCupoMaximo() > 1) {
            double factor = 1.0 + ((reserva.getCupoMaximo() - 1) * FACTOR_BONIFICACION_CUPO_GRUPAL);
            honorario = honorario.multiplicarPor(factor);
        }

        // Compensación tecnológica por conectividad en modalidad virtual
        if (reserva.getModalidad() == ModalidadTutoria.VIRTUAL && reserva.getRecordatorioMinutos() > 0) {
            honorario = honorario.sumar(COMPENSACION_CONECTIVIDAD_VIRTUAL);
        }

        return honorario;
    }
}
