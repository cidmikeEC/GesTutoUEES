package edu.uees.tutorias.notification;

/**
 * ConcreteCreator para instanciar {@link NotificadorTeams}.
 * Evidencia la extensibilidad del sistema bajo el Principio Abierto/Cerrado (OCP).
 */
public class CreadorNotificadorTeams extends CreadorNotificador {

    @Override
    public Notificador crearNotificador() {
        return new NotificadorTeams();
    }
}
