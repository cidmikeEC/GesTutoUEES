package edu.uees.tutorias.events;

import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.Notificador;

/**
 * Observador concreto que despacha avisos a través de un canal de notificación.
 * Reutiliza e integra la jerarquía del patrón Factory Method desarrollada en Ae2,
 * demostrando coherencia y evolución entre incrementos.
 */
public class NotificacionReservaObserver implements ReservaObserver {

    private final Notificador notificador;

    public NotificacionReservaObserver(Notificador notificador) {
        if (notificador == null) {
            throw new IllegalArgumentException("El notificador no puede ser nulo");
        }
        this.notificador = notificador;
    }

    @Override
    public void onEvento(EventoReserva evento) {
        Reserva reserva = evento.getReserva();
        switch (evento.getTipo()) {
            case CREADA:
            case CONFIRMADA:
                notificador.notificarReservaCreada(reserva);
                break;
            case CANCELADA:
                notificador.notificarReservaCancelada(reserva);
                break;
            default:
                break;
        }
    }

    public Notificador getNotificador() {
        return notificador;
    }

    @Override
    public String getNombre() {
        return "NotificacionReservaObserver(" + notificador.getCanal() + ")";
    }
}
