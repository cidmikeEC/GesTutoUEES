package edu.uees.tutorias.notification;

/**
 * ConcreteCreator para instanciar {@link NotificadorSMS}.
 */
public class CreadorNotificadorSMS extends CreadorNotificador {

    @Override
    public Notificador crearNotificador() {
        return new NotificadorSMS();
    }
}
