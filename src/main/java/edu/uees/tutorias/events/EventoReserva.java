package edu.uees.tutorias.events;

import edu.uees.tutorias.domain.Reserva;
import java.time.LocalDateTime;

/**
 * Objeto inmutable de evento que encapsula la información de una transición
 * de estado o acción ocurrida sobre una reserva.
 */
public class EventoReserva {

    private final TipoEventoReserva tipo;
    private final Reserva reserva;
    private final LocalDateTime timestamp;
    private final String detalle;

    public EventoReserva(TipoEventoReserva tipo, Reserva reserva, String detalle) {
        this.tipo = tipo;
        this.reserva = reserva;
        this.timestamp = LocalDateTime.now();
        this.detalle = detalle;
    }

    public TipoEventoReserva getTipo() {
        return tipo;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDetalle() {
        return detalle;
    }

    @Override
    public String toString() {
        return String.format("[%s] Evento: %s | Reserva: %s (%s) | Detalle: %s",
                timestamp, tipo, reserva.getId(), reserva.getMateria(), detalle);
    }
}
