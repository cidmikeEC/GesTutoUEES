package edu.uees.tutorias.notification;

/**
 * ConcreteCreator para instanciar {@link NotificadorWhatsApp}.
 */
public class CreadorNotificadorWhatsApp extends CreadorNotificador {

    @Override
    public Notificador crearNotificador() {
        return new NotificadorWhatsApp();
    }
}
