package edu.uees.tutorias;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.EstadoReserva;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.ModalidadTutoria;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.domain.ReservaBuilder;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReservaBuilderTest {

    private Estudiante estudiante;
    private Horario horario;

    @BeforeEach
    void setUp() {
        Docente docente = new Docente("DOC-01", "Lessette Zambrano", "lzambrano@uees.edu.ec", "Estructura de Datos");
        estudiante = new Estudiante("EST-01", "Miguel Delgado", "mdelgado@uees.edu.ec", "Computación");
        horario = new Horario("HOR-01", docente,
                LocalDateTime.of(2026, 9, 5, 14, 0),
                LocalDateTime.of(2026, 9, 5, 15, 0));
    }

    @Test
    @DisplayName("Construcción básica virtual: aplica valores por defecto sensatos")
    void testConstruccionVirtualDefaults() {
        Reserva reserva = Reserva.builder()
                .conId("RES-VIRT-01")
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Diseño de Software")
                .conTema("Diagramas de Secuencia UML")
                .build();

        assertEquals("RES-VIRT-01", reserva.getId());
        assertEquals(estudiante, reserva.getEstudiante());
        assertEquals(horario, reserva.getHorario());
        assertEquals("Diseño de Software", reserva.getMateria());
        assertEquals("Diagramas de Secuencia UML", reserva.getTema());
        assertEquals(ModalidadTutoria.VIRTUAL, reserva.getModalidad());
        assertNotNull(reserva.getEnlaceVirtual());
        assertTrue(reserva.getEnlaceVirtual().contains("teams.microsoft.com"));
        assertEquals(30, reserva.getRecordatorioMinutos());
        assertFalse(reserva.isEsGrupal());
        assertEquals(1, reserva.getCupoMaximo());
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
    }

    @Test
    @DisplayName("Construcción completa presencial y grupal con observaciones")
    void testConstruccionPresencialGrupal() {
        Reserva reserva = Reserva.builder()
                .conId("RES-PRES-02")
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Estructura de Datos")
                .conTema("Árboles AVL y Rotaciones")
                .modalidadPresencial("Laboratorio de Computación LAB-302")
                .grupal(5)
                .conRecordatorioMinutos(15)
                .conObservaciones("Traer laptops con JDK 17 instalado")
                .build();

        assertEquals(ModalidadTutoria.PRESENCIAL, reserva.getModalidad());
        assertEquals("Laboratorio de Computación LAB-302", reserva.getAula());
        assertTrue(reserva.isEsGrupal());
        assertEquals(5, reserva.getCupoMaximo());
        assertEquals(15, reserva.getRecordatorioMinutos());
        assertEquals("Traer laptops con JDK 17 instalado", reserva.getObservaciones());
    }

    @Test
    @DisplayName("Validación: Debe fallar si falta el Estudiante")
    void testValidacionFaltaEstudiante() {
        ReservaBuilder builder = Reserva.builder()
                .conId("RES-ERR-1")
                .enHorario(horario)
                .conMateria("Diseño de Software")
                .conTema("Patrones");

        IllegalStateException ex = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(ex.getMessage().contains("Estudiante"));
    }

    @Test
    @DisplayName("Validación: Debe fallar si falta el Horario")
    void testValidacionFaltaHorario() {
        ReservaBuilder builder = Reserva.builder()
                .conId("RES-ERR-2")
                .paraEstudiante(estudiante)
                .conMateria("Diseño de Software")
                .conTema("Patrones");

        IllegalStateException ex = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(ex.getMessage().contains("Horario"));
    }

    @Test
    @DisplayName("Validación: Modalidad presencial sin aula debe lanzar excepción")
    void testValidacionPresencialSinAula() {
        ReservaBuilder builder = Reserva.builder()
                .conId("RES-ERR-3")
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Diseño de Software")
                .conTema("Patrones")
                .modalidadPresencial(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(ex.getMessage().contains("aula"));
    }

    @Test
    @DisplayName("Validación: Tutoría grupal con cupo <= 1 debe fallar")
    void testValidacionGrupalCupoInvalido() {
        ReservaBuilder builder = Reserva.builder()
                .conId("RES-ERR-4")
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Diseño de Software")
                .conTema("Patrones")
                .grupal(1);

        IllegalStateException ex = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(ex.getMessage().contains("al menos 2"));
    }

    @Test
    @DisplayName("Validación: Recordatorio con minutos negativos debe fallar")
    void testValidacionRecordatorioNegativo() {
        ReservaBuilder builder = Reserva.builder()
                .conId("RES-ERR-5")
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Diseño de Software")
                .conTema("Patrones")
                .conRecordatorioMinutos(-10);

        IllegalStateException ex = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(ex.getMessage().contains("negativo"));
    }
}
