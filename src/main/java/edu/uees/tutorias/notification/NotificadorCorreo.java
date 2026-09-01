package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.ModalidadTutoria;
import edu.uees.tutorias.domain.Reserva;

/**
 * ConcreteProduct: Notificaciones formales mediante Correo Electrónico institucional UEES.
 */
public class NotificadorCorreo implements Notificador {

    @Override
    public void notificarReservaCreada(Reserva reserva) {
        String ubicacion = reserva.getModalidad() == ModalidadTutoria.VIRTUAL 
                ? "Enlace: " + reserva.getEnlaceVirtual() 
                : "Aula: " + reserva.getAula();

        System.out.println("[EMAIL institucional] Para: " + reserva.getEstudiante().getCorreo()
                + " y " + reserva.getHorario().getDocente().getCorreo()
                + "\n  ├─ Asunto: Tutoría Académica Confirmada #" + reserva.getId()
                + "\n  ├─ Materia: " + reserva.getMateria() + " | Tema: " + reserva.getTema()
                + "\n  ├─ " + ubicacion
                + "\n  └─ Recordatorio: " + reserva.getRecordatorioMinutos() + " min antes.\n");
    }

    @Override
    public void notificarReservaCancelada(Reserva reserva) {
        System.out.println("[EMAIL institucional] Para: " + reserva.getEstudiante().getCorreo()
                + "\n  ├─ Asunto: Tutoría Cancelada #" + reserva.getId()
                + "\n  └─ El horario ha sido liberado exitosamente.\n");
    }

    @Override
    public String getCanal() {
        return "CORREO";
    }
}