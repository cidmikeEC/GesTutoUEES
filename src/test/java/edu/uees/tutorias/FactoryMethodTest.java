package edu.uees.tutorias;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.CreadorNotificador;
import edu.uees.tutorias.notification.CreadorNotificadorCorreo;
import edu.uees.tutorias.notification.CreadorNotificadorSMS;
import edu.uees.tutorias.notification.CreadorNotificadorTeams;
import edu.uees.tutorias.notification.CreadorNotificadorWhatsApp;
import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.notification.NotificadorCorreo;
import edu.uees.tutorias.notification.NotificadorSMS;
import edu.uees.tutorias.notification.NotificadorTeams;
import edu.uees.tutorias.notification.NotificadorWhatsApp;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FactoryMethodTest {

    private Reserva reservaDemo;

    @BeforeEach
    void setUp() {
        Docente docente = new Docente("DOC-01", "Lessette Zambrano", "lzambrano@uees.edu.ec", "Estructura de Datos");
        Estudiante estudiante = new Estudiante("EST-01", "Miguel Delgado", "mdelgado@uees.edu.ec", "Computación");
        Horario horario = new Horario("HOR-01", docente,
                LocalDateTime.of(2026, 9, 2, 10, 0),
                LocalDateTime.of(2026, 9, 2, 11, 0));

        reservaDemo = Reserva.builder()
                .conId("RES-TEST-01")
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Diseño de Software")
                .conTema("Patrones Creacionales GoF")
                .modalidadVirtual()
                .build();
    }

    @Test
    @DisplayName("CreadorNotificadorCorreo debe instanciar NotificadorCorreo")
    void testCreadorCorreo() {
        CreadorNotificador creador = new CreadorNotificadorCorreo();
        Notificador notificador = creador.crearNotificador();

        assertNotNull(notificador);
        assertInstanceOf(NotificadorCorreo.class, notificador);
        assertEquals("CORREO", notificador.getCanal());
        assertDoesNotThrow(() -> creador.enviarNotificacionReserva(reservaDemo));
    }

    @Test
    @DisplayName("CreadorNotificadorSMS debe instanciar NotificadorSMS")
    void testCreadorSMS() {
        CreadorNotificador creador = new CreadorNotificadorSMS();
        Notificador notificador = creador.crearNotificador();

        assertNotNull(notificador);
        assertInstanceOf(NotificadorSMS.class, notificador);
        assertEquals("SMS", notificador.getCanal());
        assertDoesNotThrow(() -> creador.enviarNotificacionReserva(reservaDemo));
    }

    @Test
    @DisplayName("CreadorNotificadorWhatsApp debe instanciar NotificadorWhatsApp")
    void testCreadorWhatsApp() {
        CreadorNotificador creador = new CreadorNotificadorWhatsApp();
        Notificador notificador = creador.crearNotificador();

        assertNotNull(notificador);
        assertInstanceOf(NotificadorWhatsApp.class, notificador);
        assertEquals("WHATSAPP", notificador.getCanal());
        assertDoesNotThrow(() -> creador.enviarNotificacionReserva(reservaDemo));
    }

    @Test
    @DisplayName("CreadorNotificadorTeams (extensión OCP) debe instanciar NotificadorTeams")
    void testCreadorTeamsExtensible() {
        CreadorNotificador creador = new CreadorNotificadorTeams();
        Notificador notificador = creador.crearNotificador();

        assertNotNull(notificador);
        assertInstanceOf(NotificadorTeams.class, notificador);
        assertEquals("MS_TEAMS", notificador.getCanal());
        assertDoesNotThrow(() -> creador.enviarNotificacionReserva(reservaDemo));
        assertDoesNotThrow(() -> creador.enviarNotificacionCancelacion(reservaDemo));
    }

    @Test
    @DisplayName("Polimorfismo: Despacho a través de lista heterogénea de creadores sin acoplamiento")
    void testPolimorfismoCreadores() {
        List<CreadorNotificador> fabricas = List.of(
                new CreadorNotificadorCorreo(),
                new CreadorNotificadorSMS(),
                new CreadorNotificadorWhatsApp(),
                new CreadorNotificadorTeams()
        );

        assertEquals(4, fabricas.size());
        for (CreadorNotificador fabrica : fabricas) {
            assertNotNull(fabrica.crearNotificador());
            assertDoesNotThrow(() -> fabrica.enviarNotificacionReserva(reservaDemo));
        }
    }
}
