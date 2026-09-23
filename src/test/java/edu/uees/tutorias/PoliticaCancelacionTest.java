package edu.uees.tutorias;

import edu.uees.tutorias.domain.Dinero;
import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.service.PoliticaCancelacion;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PoliticaCancelacion · Refactorización 3 (Ae5)")
class PoliticaCancelacionTest {

    private PoliticaCancelacion politica;
    private Docente docente;
    private Estudiante estudiante;

    @BeforeEach
    void setUp() {
        politica = new PoliticaCancelacion();
        docente = new Docente("DOC-01", "Lessette Zambrano", "lzambrano@uees.edu.ec", "Estructura de Datos");
        estudiante = new Estudiante("EST-01", "Miguel Delgado", "mdelgado@uees.edu.ec", "Computación");
    }

    @Test
    @DisplayName("Debe detectar cancelación tardía cuando anticipación es menor a 24 horas")
    void testCancelacionTardia() {
        LocalDateTime creacion = LocalDateTime.of(2026, 9, 22, 10, 0);
        LocalDateTime inicio = LocalDateTime.of(2026, 9, 22, 18, 0); // 8 horas

        Horario h = new Horario("H-01", docente, inicio, inicio.plusHours(1));
        Reserva r = Reserva.builder()
                .conId("R-TARDIA")
                .paraEstudiante(estudiante)
                .enHorario(h)
                .conMateria("Estructura de Datos")
                .conTema("Árboles")
                .creadaEn(creacion)
                .build();

        assertTrue(politica.esCancelacionTardia(r));
    }

    @Test
    @DisplayName("Debe detectar cancelación oportuna cuando anticipación supera 24 horas")
    void testCancelacionOportuna() {
        LocalDateTime creacion = LocalDateTime.of(2026, 9, 20, 10, 0);
        LocalDateTime inicio = LocalDateTime.of(2026, 9, 23, 10, 0); // 72 horas

        Horario h = new Horario("H-02", docente, inicio, inicio.plusHours(1));
        Reserva r = Reserva.builder()
                .conId("R-OPORTUNA")
                .paraEstudiante(estudiante)
                .enHorario(h)
                .conMateria("Estructura de Datos")
                .conTema("Listas")
                .creadaEn(creacion)
                .build();

        assertFalse(politica.esCancelacionTardia(r));
    }

    @Test
    @DisplayName("Debe calcular montos de penalización, compensación y deducción administrativa")
    void testCalculosPenalizacion() {
        Dinero penalizacion = politica.calcularMontoPenalizacion(); // 15.0 * 50% = 7.50
        assertEquals(Dinero.de(7.50), penalizacion);

        Dinero compensacion = politica.calcularCompensacionDocente(penalizacion); // 7.50 * 50% = 3.75
        assertEquals(Dinero.de(3.75), compensacion);

        Dinero deduccion = politica.calcularDeduccionAdministrativa(penalizacion); // 7.50 * 10% = 0.75
        assertEquals(Dinero.de(0.75), deduccion);
    }
}
