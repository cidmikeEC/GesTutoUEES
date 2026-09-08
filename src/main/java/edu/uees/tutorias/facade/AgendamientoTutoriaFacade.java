package edu.uees.tutorias.facade;

import edu.uees.tutorias.adapter.ProveedorVideoconferencia;
import edu.uees.tutorias.adapter.ReunionVirtual;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.domain.ReservaBuilder;
import edu.uees.tutorias.service.ServicioReservas;
import java.util.List;
import java.util.UUID;

/**
 * Fachada (Facade) que simplifica el subsistema de agendamiento y gestión de tutorías.
 *
 * Oculta la complejidad de interactuar directamente con:
 * - El {@link ReservaBuilder} para la construcción fluida y validada.
 * - El {@link ProveedorVideoconferencia} (Adapter) para generar salas virtuales remotas.
 * - El {@link ServicioReservas} para validaciones de horario, persistencia y despacho de eventos.
 *
 * Proporciona una interfaz unificada y de alto nivel ideal para clientes de consola,
 * controladores web REST o interfaces gráficas.
 */
public class AgendamientoTutoriaFacade {

    private final ServicioReservas servicioReservas;
    private final ProveedorVideoconferencia proveedorVideo;

    public AgendamientoTutoriaFacade(ServicioReservas servicioReservas, ProveedorVideoconferencia proveedorVideo) {
        if (servicioReservas == null) {
            throw new IllegalArgumentException("El servicio de reservas no puede ser nulo");
        }
        this.servicioReservas = servicioReservas;
        this.proveedorVideo = proveedorVideo;
    }

    /**
     * Agendamiento simplificado de una tutoría en modalidad VIRTUAL:
     * 1. Valida el horario antes de proceder.
     * 2. Construye una reserva preliminar para suministrar contexto al adaptador de videoconferencia.
     * 3. Solicita la creación de la sala virtual mediante el adaptador (Zoom/Teams).
     * 4. Ensambla la reserva definitiva incorporando el enlace oficial.
     * 5. Registra y confirma la reserva mediante el servicio de dominio.
     */
    public Reserva agendarTutoriaVirtual(Estudiante estudiante, Horario horario,
                                         String materia, String tema, int recordatorioMinutos) {
        if (!horario.estaDisponible()) {
            throw new IllegalStateException("El horario seleccionado ya no se encuentra disponible.");
        }

        String idReserva = generarIdReserva();

        // Construcción preliminar para obtener datos contextuales del proveedor externo
        Reserva temp = Reserva.builder()
                .conId(idReserva)
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria(materia)
                .conTema(tema)
                .modalidadVirtual()
                .conRecordatorioMinutos(recordatorioMinutos)
                .build();

        String urlSala = "https://tutorias.uees.edu.ec/sala-virtual-default";
        if (proveedorVideo != null) {
            ReunionVirtual reunion = proveedorVideo.crearSala(temp);
            urlSala = reunion.getUrlAcceso();
        }

        // Construcción definitiva enriquecida con el enlace de la sala
        Reserva reservaDefinitiva = Reserva.builder()
                .conId(idReserva)
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria(materia)
                .conTema(tema)
                .modalidadVirtual()
                .conEnlaceVirtual(urlSala)
                .conRecordatorioMinutos(recordatorioMinutos)
                .build();

        return servicioReservas.registrarReserva(reservaDefinitiva);
    }

    /**
     * Agendamiento simplificado de una tutoría en modalidad PRESENCIAL en el campus.
     */
    public Reserva agendarTutoriaPresencial(Estudiante estudiante, Horario horario,
                                           String materia, String tema, String aulaFisica) {
        String idReserva = generarIdReserva();

        Reserva reserva = Reserva.builder()
                .conId(idReserva)
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria(materia)
                .conTema(tema)
                .modalidadPresencial()
                .conAula(aulaFisica)
                .build();

        return servicioReservas.registrarReserva(reserva);
    }

    /**
     * Cancela una tutoría activa por su ID.
     */
    public void cancelarTutoria(String idReserva) {
        servicioReservas.cancelar(idReserva);
    }

    /**
     * Devuelve la lista completa de reservas administradas.
     */
    public List<Reserva> listarTodasLasReservas() {
        return servicioReservas.listarReservas();
    }

    public ServicioReservas getServicioReservas() {
        return servicioReservas;
    }

    public ProveedorVideoconferencia getProveedorVideo() {
        return proveedorVideo;
    }

    private String generarIdReserva() {
        return "RES-INC1-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
