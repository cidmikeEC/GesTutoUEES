package edu.uees.tutorias;

import edu.uees.tutorias.domain.Dinero;
import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.service.CalculadorHonorariosDocente;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CalculadorHonorariosDocente · Refactorización 2 (Ae5)")
class CalculadorHonorariosDocenteTest {

    private CalculadorHonorariosDocente calculador;
    private Docente docenteEspecializado;
    private Docente docenteGeneral;
    private Estudiante estudiante;

    @BeforeEach
    void setUp() {
        calculador = new CalculadorHonorariosDocente();
        docenteEspecializado = new Docente("DOC-ED", "Lessette Zambrano", "lzambrano@uees.edu.ec", "Estructura de Datos");
        docenteGeneral = new Docente("DOC-GEN", "Carlos Mendoza", "cmendoza@uees.edu.ec", "Álgebra Lineal");
        estudiante = new Estudiante("EST-01", "Miguel Delgado", "mdelgado@uees.edu.ec", "Computación");
    }

    @Test
    @DisplayName("Debe asignar tarifa especializada para materias clave y base para generales")
    void testTarifasPorEspecialidad() {
        assertEquals(Dinero.de(30.0), calculador.obtenerTarifaBase(docenteEspecializado));
        assertEquals(Dinero.de(25.0), calculador.obtenerTarifaBase(docenteGeneral));
    }

    @Test
    @DisplayName("Debe calcular honorario virtual con compensación tecnológica")
    void testHonorarioVirtualConConectividad() {
        Horario horario = new Horario("H-01", docenteEspecializado, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        Reserva r = Reserva.builder()
                .conId("R-01")
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Estructura de Datos")
                .conTema("Árboles")
                .modalidadVirtual()
                .conRecordatorioMinutos(15)
                .build();

        Dinero honorario = calculador.calcularHonorarioConfirmada(r, docenteEspecializado);
        assertEquals(Dinero.de(32.50), honorario);
    }

    @Test
    @DisplayName("Debe calcular bonificación por tutoría grupal")
    void testHonorarioGrupal() {
        Horario horario = new Horario("H-02", docenteEspecializado, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        Reserva r = Reserva.builder()
                .conId("R-02")
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Diseño de Software")
                .conTema("Patrones")
                .modalidadPresencial("LAB-101")
                .grupal(3) // 2 adicionales * 15% = +30%
                .build();

        Dinero honorario = calculador.calcularHonorarioConfirmada(r, docenteEspecializado);
        assertEquals(Dinero.de(39.00), honorario);
    }
}
