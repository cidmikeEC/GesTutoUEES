package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.Reserva;

/**
 * Implementación por correo electrónico.
 * Por ahora solo simula el envío por consola (no hay servidor SMTP en esta
 * etapa), pero cumple el contrato de Notificador y deja el punto de extensión.
 */
public class NotificadorCorreo implements Notificador {

    @Override
    public void notificarReservaCreada(Reserva reserva) {
        System.out.println("  [CORREO] -> " + reserva.getHorario().getDocente().getCorreo()
                + ": nueva reserva de " + reserva.getEstudiante().getNombre()
                + " para el " + reserva.getHorario().getId());
    }

    @Override
    public void notificarReservaCancelada(Reserva reserva) {
        System.out.println("  [CORREO] -> " + reserva.getHorario().getDocente().getCorreo()
                + ": la reserva " + reserva.getId() + " fue cancelada");
    }
}