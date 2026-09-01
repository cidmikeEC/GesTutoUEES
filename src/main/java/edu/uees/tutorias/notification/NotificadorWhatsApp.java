package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.ModalidadTutoria;
import edu.uees.tutorias.domain.Reserva;

/**
 * ConcreteProduct: Notificaciones interactivas vía WhatsApp Business API.
 */
public class NotificadorWhatsApp implements Notificador {

    @Override
    public void notificarReservaCreada(Reserva reserva) {
        String infoLugar = reserva.getModalidad() == ModalidadTutoria.VIRTUAL
                ? "🔗 *Enlace:* " + reserva.getEnlaceVirtual()
                : "🏛️ *Aula:* " + reserva.getAula();

        System.out.println("[WHATSAPP] 💬 Mensaje a " + reserva.getEstudiante().getNombre()
                + "\n  *¡Tu tutoría UEES está lista!*"
                + "\n  📚 *Materia:* " + reserva.getMateria()
                + "\n  👨‍🏫 *Docente:* " + reserva.getHorario().getDocente().getNombre()
                + "\n  " + infoLugar
                + "\n  ⏰ *Inicio:* " + reserva.getHorario().getInicio()
                + "\n  (Responde CANCELAR si no podrás asistir)\n");
    }

    @Override
    public void notificarReservaCancelada(Reserva reserva) {
        System.out.println("[WHATSAPP] 💬 Mensaje a " + reserva.getEstudiante().getNombre()
                + "\n  ⚠️ *Tutoría #" + reserva.getId() + " cancelada.*"
                + "\n  El horario ha quedado libre en la plataforma.\n");
    }

    @Override
    public String getCanal() {
        return "WHATSAPP";
    }
}
