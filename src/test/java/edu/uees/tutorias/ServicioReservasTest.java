package edu.uees.tutorias;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.CreadorNotificadorTeams;
import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.persistence.RepositorioReservas;
import edu.uees.tutorias.persistence.RepositorioReservasEnMemoria;
import edu.uees.tutorias.service.ServicioReservas;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServicioReservasTest {

    private RepositorioReservas repositorio;
    private Notificador notificador;
    private ServicioReservas servicio;
    private Docente docente;
    private Estudiante estudiante;
    private Horario horario;

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioReservasEnMemoria();
        notificador = new CreadorNotificadorTeams().crearNotificador();
        servicio = new ServicioReservas(repositorio, notificador);

        docente = new Docente("DOC-01", "Lessette Zambrano", "lzambrano@uees.edu.ec", "Estructura de Datos");
        estudiante = new Estudiante("EST-01", "Miguel Delgado", "mdelgado@uees.edu.ec", "Computación");
        horario = new Horario("HOR-01", docente,
                LocalDateTime.of(2026, 9, 10, 15, 0),
                LocalDateTime.of(2026, 9, 10, 16, 0));
    }

    @Test
    @DisplayName("Registrar reserva construida con Builder bloquea horario y la guarda")
    void testRegistrarReservaConBuilder() {
        assertTrue(horario.estaDisponible());

        Reserva reserva = Reserva.builder()
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Diseño de Software")
                .conTema("Factory Method y Builder")
                .modalidadVirtual()
                .build();

        Reserva registrada = servicio.registrarReserva(reserva);

        assertNotNull(registrada);
        assertFalse(horario.estaDisponible());
        assertEquals(1, servicio.listarReservas().size());
    }

    @Test
    @DisplayName("Regla anti doble reserva: no permite reservar horario ocupado")
    void testAntiDobleReserva() {
        servicio.reservar(estudiante, horario);
        assertFalse(horario.estaDisponible());

        Estudiante otro = new Estudiante("EST-02", "Rubí Floreano", "rfloreano@uees.edu.ec", "Computación");
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> servicio.reservar(otro, horario));
        assertTrue(ex.getMessage().contains("ocupado"));
    }

    @Test
    @DisplayName("Cancelar reserva libera el horario asociado")
    void testCancelarLiberaHorario() {
        Reserva r = servicio.reservar(estudiante, horario);
        assertFalse(horario.estaDisponible());

        servicio.cancelar(r.getId());
        assertTrue(horario.estaDisponible());
    }
}
