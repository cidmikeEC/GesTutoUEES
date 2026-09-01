package edu.uees.tutorias.service;

import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.persistence.RepositorioReservas;
import java.util.List;
import java.util.UUID;

/**
 * Orquesta la lógica de negocio de las reservas de tutorías.
 * Coordina la verificación de horarios, persistencia y despacho de notificaciones.
 *
 * Cumple con DIP (Dependency Inversion Principle) al depender de abstracciones
 * ({@link RepositorioReservas} y {@link Notificador}), permitiendo intercambiar
 * implementaciones o canales de notificación en tiempo de ejecución.
 */
public class ServicioReservas {

    private final RepositorioReservas repositorio;
    private final Notificador notificador;

    public ServicioReservas(RepositorioReservas repositorio, Notificador notificador) {
        this.repositorio = repositorio;
        this.notificador = notificador;
    }

    /**
     * Registra una reserva previamente construida (por ejemplo, mediante {@link edu.uees.tutorias.domain.ReservaBuilder}).
     * Valida disponibilidad, bloquea el horario, confirma y notifica.
     */
    public Reserva registrarReserva(Reserva reserva) {
        Horario horario = reserva.getHorario();
        if (!horario.estaDisponible()) {
            throw new IllegalStateException("El horario " + horario.getId() + " ya está ocupado");
        }
        horario.ocupar();
        reserva.confirmar();
        repositorio.guardar(reserva);
        notificador.notificarReservaCreada(reserva);
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

    /** Cancela una reserva: libera el horario y emite el aviso correspondiente. */
    public void cancelar(String idReserva) {
        Reserva reserva = repositorio.buscarPorId(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("No existe la reserva " + idReserva));
        reserva.cancelar(); // La propia entidad libera su horario internamente
        notificador.notificarReservaCancelada(reserva);
    }

    public List<Reserva> listarReservas() {
        return repositorio.listarTodas();
    }

    public Notificador getNotificador() {
        return notificador;
    }

    private String generarId() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}