package edu.uees.tutorias;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.events.AuditoriaReservaObserver;
import edu.uees.tutorias.events.EventoReserva;
import edu.uees.tutorias.events.GestorEventosReserva;
import edu.uees.tutorias.events.NotificacionReservaObserver;
import edu.uees.tutorias.events.ReservaObserver;
import edu.uees.tutorias.events.SincronizacionCalendarioObserver;
import edu.uees.tutorias.events.TipoEventoReserva;
import edu.uees.tutorias.notification.CreadorNotificadorCorreo;
import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.persistence.RepositorioReservas;
import edu.uees.tutorias.persistence.RepositorioReservasEnMemoria;
import edu.uees.tutorias.service.ServicioReservas;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas del Patrón Observer (Gestión reactiva de eventos de reserva)")
class ObserverTest {

    private GestorEventosReserva gestorEventos;
    private AuditoriaReservaObserver auditoriaObserver;
    private SincronizacionCalendarioObserver calendarioObserver;
    private NotificacionReservaObserver notificacionObserver;
    private RepositorioReservas repositorio;
    private ServicioReservas servicio;
    private Docente docente;
    private Estudiante estudiante;
    private Horario horario;

    @BeforeEach
    void setUp() {
        gestorEventos = new GestorEventosReserva();
        auditoriaObserver = new AuditoriaReservaObserver();
        calendarioObserver = new SincronizacionCalendarioObserver();
        Notificador notificador = new CreadorNotificadorCorreo().crearNotificador();
        notificacionObserver = new NotificacionReservaObserver(notificador);

        repositorio = new RepositorioReservasEnMemoria();
        servicio = new ServicioReservas(repositorio, gestorEventos);

        docente = new Docente("DOC-OBS", "Lessette Zambrano", "lzambrano@uees.edu.ec", "Estructura de Datos");
        estudiante = new Estudiante("EST-OBS", "Miguel Delgado", "mdelgado@uees.edu.ec", "Computación");
        horario = new Horario("HOR-OBS", docente,
                LocalDateTime.of(2026, 9, 15, 14, 0),
                LocalDateTime.of(2026, 9, 15, 15, 0));
    }

    @Test
    @DisplayName("Suscripción y conteo de observadores en el gestor")
    void testSuscripcionYDesuscripcion() {
        assertEquals(0, gestorEventos.getCantidadObservadores());

        gestorEventos.suscribir(auditoriaObserver);
        gestorEventos.suscribir(calendarioObserver);
        gestorEventos.suscribir(notificacionObserver);
        assertEquals(3, gestorEventos.getCantidadObservadores());

        gestorEventos.desuscribir(calendarioObserver);
        assertEquals(2, gestorEventos.getCantidadObservadores());
        assertFalse(gestorEventos.getObservadores().contains(calendarioObserver));
    }

    @Test
    @DisplayName("Registrar reserva dispara eventos a todos los observadores suscritos")
    void testDisparoEventosAlRegistrarReserva() {
        gestorEventos.suscribir(auditoriaObserver);
        gestorEventos.suscribir(calendarioObserver);
        gestorEventos.suscribir(notificacionObserver);

        Reserva reserva = Reserva.builder()
                .conId("RES-OBS-100")
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Diseño de Software")
                .conTema("Patrón Observer")
                .modalidadVirtual()
                .build();

        servicio.registrarReserva(reserva);

        // Validar que la bitácora de auditoría recibió el registro
        assertEquals(1, auditoriaObserver.getCantidadRegistros());
        assertTrue(auditoriaObserver.getBitacora().get(0).contains("CONFIRMADA"));
        assertTrue(auditoriaObserver.getBitacora().get(0).contains("RES-OBS-100"));

        // Validar que el calendario fue sincronizado
        assertEquals(1, calendarioObserver.getCantidadSincronizaciones());
        assertTrue(calendarioObserver.getEventosSincronizados().get(0).contains("lzambrano@uees.edu.ec"));
    }

    @Test
    @DisplayName("Cancelar reserva dispara evento de CANCELADA con actualización de bitácora y calendario")
    void testDisparoEventosAlCancelarReserva() {
        gestorEventos.suscribir(auditoriaObserver);
        gestorEventos.suscribir(calendarioObserver);

        Reserva reserva = Reserva.builder()
                .conId("RES-OBS-200")
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Diseño de Software")
                .conTema("Patrón Observer Cancelación")
                .modalidadVirtual()
                .build();

        servicio.registrarReserva(reserva);
        servicio.cancelar("RES-OBS-200");

        // Deben registrarse 2 eventos en auditoría (CONFIRMADA + CANCELADA)
        assertEquals(2, auditoriaObserver.getCantidadRegistros());
        assertTrue(auditoriaObserver.getBitacora().get(1).contains("CANCELADA"));

        // Deben registrarse 2 operaciones en calendario (Agendado + Eliminado)
        assertEquals(2, calendarioObserver.getCantidadSincronizaciones());
        assertTrue(calendarioObserver.getEventosSincronizados().get(1).contains("eliminado"));
    }

    @Test
    @DisplayName("Observador anónimo personalizado para verificar extensibilidad OCP")
    void testObservadorPersonalizadoExtensibilidad() {
        List<EventoReserva> eventosCapturados = new ArrayList<>();
        ReservaObserver observadorCustom = eventosCapturados::add;

        gestorEventos.suscribir(observadorCustom);

        Reserva reserva = Reserva.builder()
                .conId("RES-OBS-300")
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Diseño de Software")
                .conTema("Extensibilidad OCP")
                .modalidadVirtual()
                .build();

        servicio.registrarReserva(reserva);

        assertEquals(1, eventosCapturados.size());
        assertEquals(TipoEventoReserva.CONFIRMADA, eventosCapturados.get(0).getTipo());
        assertEquals("RES-OBS-300", eventosCapturados.get(0).getReserva().getId());
    }
}
