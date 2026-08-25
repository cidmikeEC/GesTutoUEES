package edu.uees.tutorias.service;

import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.persistence.RepositorioReservas;
import java.util.List;
import java.util.UUID;

/**
 * Orquesta la lógica de negocio de las reservas.
 * Es la única clase que coordina horarios, reservas, persistencia y avisos.
 *
 * Depende de ABSTRACCIONES (Notificador, RepositorioReservas), no de
 * implementaciones concretas: eso es el Dependency Inversion Principle.
 * Las dependencias llegan por el constructor (inyección), así que el servicio
 * no las crea ni las conoce por dentro.
 */
public class ServicioReservas {

    private final RepositorioReservas repositorio;
    private final Notificador notificador;

    public ServicioReservas(RepositorioReservas repositorio, Notificador notificador) {
        this.repositorio = repositorio;
        this.notificador = notificador;
    }

    /**
     * Reserva un horario para un estudiante.
     * Regla de negocio clave: un horario ocupado NO se puede volver a reservar.
     */
    public Reserva reservar(Estudiante estudiante, Horario horario) {
        if (!horario.estaDisponible()) {
            throw new IllegalStateException(
                    "El horario " + horario.getId() + " ya está ocupado");
        }
        horario.ocupar(); // primero bloqueo el horario (evita doble reserva)

        Reserva reserva = new Reserva(generarId(), estudiante, horario);
        reserva.confirmar();
        repositorio.guardar(reserva);
        notificador.notificarReservaCreada(reserva);
        return reserva;
    }

    /** Cancela una reserva: libera el horario y avisa al docente. */
    public void cancelar(String idReserva) {
        Reserva reserva = repositorio.buscarPorId(idReserva)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la reserva " + idReserva));
        reserva.cancelar(); // la propia reserva libera su horario
        notificador.notificarReservaCancelada(reserva);
    }

    public List<Reserva> listarReservas() {
        return repositorio.listarTodas();
    }

    private String generarId() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}