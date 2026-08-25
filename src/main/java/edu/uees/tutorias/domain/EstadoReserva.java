package edu.uees.tutorias.domain;

/**
 * Los estados por los que pasa una reserva.
 * Un enum (no un String suelto) para que solo existan estos valores válidos.
 */
public enum EstadoReserva {
    PENDIENTE,
    CONFIRMADA,
    CANCELADA,
    REPROGRAMADA,
    REALIZADA
}