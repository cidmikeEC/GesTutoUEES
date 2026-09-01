package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.Reserva;

/**
 * ConcreteProduct: Canal extensible que publica una tarjeta adaptativa (Adaptive Card)
 * y agenda un evento en el calendario de Microsoft Teams institucional.
 */
public class NotificadorTeams implements Notificador {

    @Override
    public void notificarReservaCreada(Reserva reserva) {
        System.out.println("[MS TEAMS BOT] 🤖 Evento de Calendario & Webhook de Canal"
                + "\n  ├─ Reunión: Tutoría " + reserva.getMateria() + " (" + reserva.getTema() + ")"
                + "\n  ├─ Asistentes: " + reserva.getEstudiante().getCorreo() + ", " + reserva.getHorario().getDocente().getCorreo()
                + "\n  ├─ Enlace de llamada: " + reserva.getEnlaceVirtual()
                + "\n  └─ Estado: Convocatoria enviada a calendario Outlook/Teams.\n");
    }

    @Override
    public void notificarReservaCancelada(Reserva reserva) {
        System.out.println("[MS TEAMS BOT] 🤖 Convocatoria eliminada del calendario de Teams para #" + reserva.getId() + "\n");
    }

    @Override
    public String getCanal() {
        return "MS_TEAMS";
    }
}
