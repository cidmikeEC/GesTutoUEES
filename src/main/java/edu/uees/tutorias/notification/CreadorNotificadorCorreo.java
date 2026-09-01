package edu.uees.tutorias.notification;

/**
 * ConcreteCreator para instanciar {@link NotificadorCorreo}.
 */
public class CreadorNotificadorCorreo extends CreadorNotificador {

    @Override
    public Notificador crearNotificador() {
        return new NotificadorCorreo();
    }
}
