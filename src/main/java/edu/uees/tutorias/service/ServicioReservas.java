package edu.uees.tutorias.service;

import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.events.EventoReserva;
import edu.uees.tutorias.events.GestorEventosReserva;
import edu.uees.tutorias.events.NotificacionReservaObserver;
import edu.uees.tutorias.events.TipoEventoReserva;
import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.persistence.RepositorioReservas;
import java.util.List;
import java.util.UUID;

/**
 * Orquesta la lógica de negocio de las reservas de tutorías.
 * Coordina la verificación de disponibilidad de horarios, persistencia en repositorio
 * y emisión desacoplada de eventos de ciclo de vida mediante el patrón Observer.
 *
 * Cumple con:
 * - SRP: Se encarga únicamente del flujo de negocio de reservas.
 * - OCP: Nuevos oyentes (auditoría, correo, calendario) se agregan como observadores sin modificar esta clase.
 * - DIP: Depende de abstracciones ({@link RepositorioReservas} y {@link GestorEventosReserva}).
 */
public class ServicioReservas {

    private final RepositorioReservas repositorio;
    private final GestorEventosReserva gestorEventos;
    private Notificador notificadorCompatibilidad;

    /**
     * Constructor principal orientado a eventos (Patrón Observer).
     */
    public ServicioReservas(RepositorioReservas repositorio, GestorEventosReserva gestorEventos) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio no puede ser nulo");
        }
        if (gestorEventos == null) {
            throw new IllegalArgumentException("El gestor de eventos no puede ser nulo");
        }
        this.repositorio = repositorio;
        this.gestorEventos = gestorEventos;
    }

    /**
     * Constructor de compatibilidad con Ae2 (encapsula el notificador como un observador).
     */
    public ServicioReservas(RepositorioReservas repositorio, Notificador notificador) {
        this(repositorio, new GestorEventosReserva());
        if (notificador != null) {
            this.notificadorCompatibilidad = notificador;
            this.gestorEventos.suscribir(new NotificacionReservaObserver(notificador));
        }
    }

    /**
     * Registra una reserva previamente construida (por ejemplo, mediante {@link edu.uees.tutorias.domain.ReservaBuilder}).
     * Valida disponibilidad, bloquea el horario, confirma, persiste y emite el evento de confirmación.
     */
    public Reserva registrarReserva(Reserva reserva) {
        Horario horario = reserva.getHorario();
        if (!horario.estaDisponible()) {
            throw new IllegalStateException("El horario " + horario.getId() + " ya está ocupado");
        }
        horario.ocupar();
        reserva.confirmar();
        repositorio.guardar(reserva);

        // Notificación reactiva desacoplada a todos los observadores
        gestorEventos.notificar(new EventoReserva(
                TipoEventoReserva.CONFIRMADA,
                reserva,
                "Reserva confirmada y persistida exitosamente"
        ));

        return reserva;
    }

    /**
     * Sobrecarga de conveniencia para registrar una reserva básica a partir de estudiante y horario.
     */
    public Reserva reservar(Estudiante estudiante, Horario horario) {
        Reserva reserva = Reserva.builder()
                .conId(generarId())
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria(horario.getDocente().getEspecialidad())
                .conTema("Consulta académica general")
                .modalidadVirtual()
                .build();
        return registrarReserva(reserva);
    }

    /**
     * Cancela una reserva: libera el horario, actualiza el estado y emite el evento correspondiente.
     */
    public void cancelar(String idReserva) {
        Reserva reserva = repositorio.buscarPorId(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("No existe la reserva " + idReserva));
        reserva.cancelar(); // La propia entidad libera su horario internamente

        // Emisión de evento para que los observadores (auditoría, correo, calendario) actúen
        gestorEventos.notificar(new EventoReserva(
                TipoEventoReserva.CANCELADA,
                reserva,
                "Reserva cancelada administrativamente o por el usuario"
        ));
    }

    public List<Reserva> listarReservas() {
        return repositorio.listarTodas();
    }

    public RepositorioReservas getRepositorio() {
        return repositorio;
    }

    public GestorEventosReserva getGestorEventos() {
        return gestorEventos;
    }

    public Notificador getNotificador() {
        return notificadorCompatibilidad;
    }

    private String generarId() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}