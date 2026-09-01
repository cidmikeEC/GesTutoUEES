package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.Reserva;

/**
 * Contrato Product en el patrón Factory Method.
 * Representa la abstracción de cualquier mecanismo o canal de notificación en el sistema.
 */
public interface Notificador {

    /**
     * Envía la notificación de creación/confirmación de una reserva.
     * @param reserva la reserva confirmada.
     */
    void notificarReservaCreada(Reserva reserva);

    /**
     * Envía la notificación de cancelación de una reserva.
     * @param reserva la reserva cancelada.
     */
    void notificarReservaCancelada(Reserva reserva);

    /**
     * Identificador del canal de comunicación (ej. CORREO, SMS, WHATSAPP, TEAMS).
     */
    String getCanal();
}