package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.Reserva;

/**
 * ConcreteProduct: Notificaciones breves de texto vía SMS.
 */
public class NotificadorSMS implements Notificador {

    @Override
    public void notificarReservaCreada(Reserva reserva) {
        System.out.println("[SMS] Móvil: " + reserva.getEstudiante().getId()
                + " | UEES Tutoria #" + reserva.getId() + " CONFIRMADA para "
                + reserva.getMateria() + " (" + reserva.getHorario().getInicio().toLocalTime() + ").\n");
    }

    @Override
    public void notificarReservaCancelada(Reserva reserva) {
        System.out.println("[SMS] Móvil: " + reserva.getEstudiante().getId()
                + " | UEES Tutoria #" + reserva.getId() + " CANCELADA.\n");
    }

    @Override
    public String getCanal() {
        return "SMS";
    }
}
