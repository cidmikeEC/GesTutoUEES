package edu.uees.tutorias;

import edu.uees.tutorias.adapter.ProveedorVideoconferencia;
import edu.uees.tutorias.adapter.ZoomServiceAdapter;
import edu.uees.tutorias.adapter.external.ZoomSdkClient;
import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.ModalidadTutoria;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.events.AuditoriaReservaObserver;
import edu.uees.tutorias.events.GestorEventosReserva;
import edu.uees.tutorias.facade.AgendamientoTutoriaFacade;
import edu.uees.tutorias.persistence.RepositorioReservas;
import edu.uees.tutorias.persistence.RepositorioReservasEnMemoria;
import edu.uees.tutorias.service.ServicioReservas;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas del Patrón Facade (Orquestación unificada de agendamiento)")
class FacadeTest {

    private AgendamientoTutoriaFacade facade;
    private AuditoriaReservaObserver auditoriaObserver;
    private ZoomSdkClient zoomSdk;
    private Docente docente;
    private Estudiante estudiante;
    private Horario horario1;
    private Horario horario2;

    @BeforeEach
    void setUp() {
        RepositorioReservas repositorio = new RepositorioReservasEnMemoria();
        GestorEventosReserva gestorEventos = new GestorEventosReserva();
        auditoriaObserver = new AuditoriaReservaObserver();
        gestorEventos.suscribir(auditoriaObserver);

        ServicioReservas servicio = new ServicioReservas(repositorio, gestorEventos);
        zoomSdk = new ZoomSdkClient("key-facade", "secret-facade");
        ProveedorVideoconferencia adapter = new ZoomServiceAdapter(zoomSdk);

        facade = new AgendamientoTutoriaFacade(servicio, adapter);

        docente = new Docente("DOC-FAC", "Lessette Zambrano", "lzambrano@uees.edu.ec", "Estructura de Datos");
        estudiante = new Estudiante("EST-FAC", "Miguel Delgado", "mdelgado@uees.edu.ec", "Computación");
        horario1 = new Horario("HOR-F1", docente,
                LocalDateTime.of(2026, 9, 22, 9, 0),
                LocalDateTime.of(2026, 9, 22, 10, 0));
        horario2 = new Horario("HOR-F2", docente,
                LocalDateTime.of(2026, 9, 22, 11, 0),
                LocalDateTime.of(2026, 9, 22, 12, 0));
    }

    @Test
    @DisplayName("Agendar tutoría virtual a través de la fachada coordina adapter, builder, servicio y eventos")
    void testAgendarTutoriaVirtualViaFacade() {
        Reserva reserva = facade.agendarTutoriaVirtual(
                estudiante,
                horario1,
                "Diseño de Software",
                "Patrones GoF Semana 4",
                15
        );

        assertNotNull(reserva);
        assertEquals(ModalidadTutoria.VIRTUAL, reserva.getModalidad());
        assertNotNull(reserva.getEnlaceVirtual());
        assertTrue(reserva.getEnlaceVirtual().startsWith("https://zoom.us/j/"));
        assertFalse(horario1.estaDisponible());

        // Validar que la fachada activó los observadores
        assertEquals(1, auditoriaObserver.getCantidadRegistros());

        // Validar que se creó la sala en Zoom
        assertEquals(1, zoomSdk.getCantidadSalasActivas());
    }

    @Test
    @DisplayName("Agendar tutoría presencial a través de la fachada valida aula y disponibilidad")
    void testAgendarTutoriaPresencialViaFacade() {
        Reserva reserva = facade.agendarTutoriaPresencial(
                estudiante,
                horario2,
                "Diseño de Software",
                "Revisión de Arquitectura",
                "Edificio F - Aula 204"
        );

        assertNotNull(reserva);
        assertEquals(ModalidadTutoria.PRESENCIAL, reserva.getModalidad());
        assertEquals("Edificio F - Aula 204", reserva.getAula());
        assertNull(reserva.getEnlaceVirtual());
        assertFalse(horario2.estaDisponible());
        assertEquals(1, facade.listarTodasLasReservas().size());
    }

    @Test
    @DisplayName("Cancelar tutoría a través de la fachada libera horario y notifica a los observadores")
    void testCancelarTutoriaViaFacade() {
        Reserva reserva = facade.agendarTutoriaVirtual(
                estudiante,
                horario1,
                "Diseño de Software",
                "Tutoría a cancelar",
                30
        );

        facade.cancelarTutoria(reserva.getId());

        assertTrue(horario1.estaDisponible());
        assertEquals(2, auditoriaObserver.getCantidadRegistros());
        assertTrue(auditoriaObserver.getBitacora().get(1).contains("CANCELADA"));
    }
}
