package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.Reserva;

/**
 * Creator (Clase Base) en el patrón Factory Method.
 * Declara el método fábrica abstracto {@link #crearNotificador()} que las subclases
 * concretas deben implementar para retornar una instancia de un {@link Notificador}.
 *
 * Contiene la lógica de negocio central que opera sobre el producto creado,
 * manteniéndose desacoplada de las clases concretas de notificación.
 */
public abstract class CreadorNotificador {

    /**
     * Método fábrica (Factory Method).
     * @return Una instancia concreta de {@link Notificador}.
     */
    public abstract Notificador crearNotificador();

    /**
     * Orquesta el envío del aviso de reserva utilizando el notificador creado por la fábrica.
     */
    public void enviarNotificacionReserva(Reserva reserva) {
        Notificador notificador = crearNotificador();
        notificador.notificarReservaCreada(reserva);
    }

    /**
     * Orquesta el envío del aviso de cancelación utilizando el notificador creado por la fábrica.
     */
    public void enviarNotificacionCancelacion(Reserva reserva) {
        Notificador notificador = crearNotificador();
        notificador.notificarReservaCancelada(reserva);
    }
}
