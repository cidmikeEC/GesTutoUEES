package edu.uees.tutorias.domain;

import java.time.LocalDateTime;

/**
 * El encuentro confirmado entre un estudiante y un horario.
 * Es una transacción: protege su propio ciclo de vida (confirmar, cancelar,
 * reprogramar) para que nadie la deje en un estado inconsistente.
 */
public class Reserva {

    private final String id;
    private final Estudiante estudiante;
    private final Horario horario;
    private final LocalDateTime creadaEn;
    private EstadoReserva estado;

    public Reserva(String id, Estudiante estudiante, Horario horario) {
        this.id = id;
        this.estudiante = estudiante;
        this.horario = horario;
        this.creadaEn = LocalDateTime.now();
        this.estado = EstadoReserva.PENDIENTE;
    }

    public String getId() {
        return id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Horario getHorario() {
        return horario;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public LocalDateTime getCreadaEn() {
        return creadaEn;
    }

    /** Confirma la reserva: solo si está pendiente. */
    public void confirmar() {
        if (estado != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException("Solo se confirma una reserva pendiente");
        }
        this.estado = EstadoReserva.CONFIRMADA;
    }

    /** Cancela la reserva y libera el horario para que otro lo tome. */
    public void cancelar() {
        if (estado == EstadoReserva.CANCELADA || estado == EstadoReserva.REALIZADA) {
            throw new IllegalStateException("No se puede cancelar una reserva " + estado);
        }
        this.estado = EstadoReserva.CANCELADA;
        this.horario.liberar();
    }

    /** Reprograma la reserva hacia un horario nuevo. */
    public void reprogramar() {
        if (estado != EstadoReserva.CONFIRMADA && estado != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException("Solo se reprograma una reserva vigente");
        }
        this.estado = EstadoReserva.REPROGRAMADA;
    }

    @Override
    public String toString() {
        return "Reserva " + id + " de " + estudiante.getNombre()
                + " -> " + horario.getId() + " [" + estado + "]";
    }
}