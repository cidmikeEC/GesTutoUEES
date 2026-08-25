package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.Reserva;

/**
 * Abstracción del envío de avisos.
 * El servicio de reservas depende de ESTA interfaz, no de un correo concreto
 * (Dependency Inversion). Si mañana el aviso sale por SMS o WhatsApp,
 * se crea otra implementación y el dominio no se toca.
 */
public interface Notificador {

    void notificarReservaCreada(Reserva reserva);

    void notificarReservaCancelada(Reserva reserva);
}