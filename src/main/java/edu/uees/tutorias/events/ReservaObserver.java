package edu.uees.tutorias.events;

/**
 * Contrato de observador (Subscriber) en el patrón Observer.
 * Cualquier componente interesado en enterarse de cambios en las reservas
 * debe implementar esta interfaz.
 */
public interface ReservaObserver {

    /**
     * Invocado cuando ocurre un evento sobre una reserva.
     *
     * @param evento Información detallada del evento y la reserva afectada.
     */
    void onEvento(EventoReserva evento);

    /**
     * Nombre descriptivo del observador para trazabilidad y logs.
     */
    default String getNombre() {
        return getClass().getSimpleName();
    }
}
