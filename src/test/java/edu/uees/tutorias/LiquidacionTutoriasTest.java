package edu.uees.tutorias;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.service.LiquidacionDocenteDTO;
import edu.uees.tutorias.service.LiquidacionTutoriasService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de pruebas automatizadas que define y custodia la Línea Base de Comportamiento (LBC)
 * para la Kata de Refactorización (Ae4).
 *
 * Cada caso captura los resultados exactos que deben permanecer 100% idénticos e inalterados
 * antes, durante y después de todas las transformaciones de refactorización.
 */
@DisplayName("Línea Base de Comportamiento · Kata de Refactorización Ae4")
class LiquidacionTutoriasTest {

    private LiquidacionTutoriasService servicioLiquidacion;
    private Docente docenteEspecializado;
    private Docente docenteGeneral;
    private Estudiante estudiante;

    @BeforeEach
    void setUp() {
        servicioLiquidacion = new LiquidacionTutoriasService();
        docenteEspecializado = new Docente("DOC-ED", "Lessette Zambrano", "lzambrano@uees.edu.ec", "Estructura de Datos");
        docenteGeneral = new Docente("DOC-GEN", "Carlos Mendoza", "cmendoza@uees.edu.ec", "Matemáticas Discretas");
        estudiante = new Estudiante("EST-01", "Miguel Delgado", "mdelgado@uees.edu.ec", "Computación");
    }

    @Test
    @DisplayName("Caso 1: Tutoría individual virtual confirmada (Tarifa especializada + conectividad)")
    void testCaso1_TutoriaIndividualVirtualConfirmada() {
        Horario horario = new Horario("HOR-01", docenteEspecializado,
                LocalDateTime.of(2026, 9, 20, 10, 0),
                LocalDateTime.of(2026, 9, 20, 11, 0));

        Reserva reserva = Reserva.builder()
                .conId("RES-C1")
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Estructura de Datos")
                .conTema("Árboles Binarios")
                .modalidadVirtual()
                .conRecordatorioMinutos(15)
                .build();
        reserva.confirmar();

        LiquidacionDocenteDTO resultado = servicioLiquidacion.procesarLiquidacionDocente(
                docenteEspecializado,
                List.of(reserva)
        );

        assertNotNull(resultado);
        assertEquals(1, resultado.getTutoriasProcesadas());
        assertEquals(32.50, resultado.getTotalHonorarios(), 0.001); // 30.00 base + 2.50 conectividad
        assertEquals(0.00, resultado.getTotalPenalizaciones(), 0.001);
        assertEquals(32.50, resultado.getTotalNeto(), 0.001);
        assertTrue(resultado.getDetalleLiquidacion().contains("RES-C1:OK:$32.50"));
    }

    @Test
    @DisplayName("Caso 2: Tutoría grupal presencial confirmada (Bonificación del 15% por cupo adicional)")
    void testCaso2_TutoriaGrupalPresencialConfirmada() {
        Horario horario = new Horario("HOR-02", docenteEspecializado,
                LocalDateTime.of(2026, 9, 21, 15, 0),
                LocalDateTime.of(2026, 9, 21, 16, 30));

        Reserva reservaGrupal = Reserva.builder()
                .conId("RES-C2")
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Diseño de Software")
                .conTema("Refactorización y Code Smells")
                .modalidadPresencial("LAB-301")
                .grupal(3) // 3 estudiantes -> (3 - 1) * 15% = 30% de bonificación
                .build();
        reservaGrupal.confirmar();

        LiquidacionDocenteDTO resultado = servicioLiquidacion.procesarLiquidacionDocente(
                docenteEspecializado,
                List.of(reservaGrupal)
        );

        assertNotNull(resultado);
        assertEquals(1, resultado.getTutoriasProcesadas());
        // 30.00 base * 1.30 = 39.00
        assertEquals(39.00, resultado.getTotalHonorarios(), 0.001);
        assertEquals(0.00, resultado.getTotalPenalizaciones(), 0.001);
        assertEquals(39.00, resultado.getTotalNeto(), 0.001);
        assertTrue(resultado.getDetalleLiquidacion().contains("RES-C2:OK:$39.00"));
    }

    @Test
    @DisplayName("Caso 3: Tutoría cancelada tardíamente con menos de 24 horas (Penalización)")
    void testCaso3_TutoriaCanceladaTardiaConPenalizacion() {
        LocalDateTime fechaCreacion = LocalDateTime.of(2026, 9, 22, 8, 0);
        LocalDateTime fechaInicioHorario = LocalDateTime.of(2026, 9, 22, 16, 0); // 8 horas de diferencia (< 24h)

        Horario horario = new Horario("HOR-03", docenteEspecializado,
                fechaInicioHorario,
                fechaInicioHorario.plusHours(1));

        Reserva reserva = Reserva.builder()
                .conId("RES-C3")
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Estructura de Datos")
                .conTema("Grafos")
                .modalidadVirtual()
                .creadaEn(fechaCreacion)
                .build();
        reserva.cancelar();

        LiquidacionDocenteDTO resultado = servicioLiquidacion.procesarLiquidacionDocente(
                docenteEspecializado,
                List.of(reserva)
        );

        assertNotNull(resultado);
        assertEquals(1, resultado.getTutoriasProcesadas());
        // Penalización calculada = 15.0 * 0.50 = 7.50. Compensación docente = 3.75.
        // Neto = 3.75 - (7.50 * 0.10) = 3.00
        assertEquals(7.50, resultado.getTotalPenalizaciones(), 0.001);
        assertEquals(3.75, resultado.getTotalHonorarios(), 0.001);
        assertEquals(3.00, resultado.getTotalNeto(), 0.001);
        assertTrue(resultado.getDetalleLiquidacion().contains("RES-C3:CANCEL_TARDIA:PEN=$7.50"));
    }

    @Test
    @DisplayName("Caso 4: Tutoría cancelada oportunamente (> 24h) y manejo de lista vacía")
    void testCaso4_TutoriaCanceladaOportunaYListaVacia() {
        LocalDateTime fechaCreacion = LocalDateTime.of(2026, 9, 20, 10, 0);
        LocalDateTime fechaInicioHorario = LocalDateTime.of(2026, 9, 23, 10, 0); // 72 horas de diferencia (> 24h)

        Horario horario = new Horario("HOR-04", docenteGeneral,
                fechaInicioHorario,
                fechaInicioHorario.plusHours(1));

        Reserva reserva = Reserva.builder()
                .conId("RES-C4")
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Matemáticas Discretas")
                .conTema("Relaciones de Recurrencia")
                .modalidadVirtual()
                .creadaEn(fechaCreacion)
                .build();
        reserva.cancelar();

        LiquidacionDocenteDTO resCancelada = servicioLiquidacion.procesarLiquidacionDocente(
                docenteGeneral,
                List.of(reserva)
        );

        assertEquals(1, resCancelada.getTutoriasProcesadas());
        assertEquals(0.00, resCancelada.getTotalHonorarios(), 0.001);
        assertEquals(0.00, resCancelada.getTotalPenalizaciones(), 0.001);
        assertEquals(0.00, resCancelada.getTotalNeto(), 0.001);
        assertTrue(resCancelada.getDetalleLiquidacion().contains("RES-C4:CANCEL_OPORTUNA:$0.00"));

        // Prueba de lista vacía
        LiquidacionDocenteDTO resVacio = servicioLiquidacion.procesarLiquidacionDocente(
                docenteGeneral,
                Collections.emptyList()
        );
        assertEquals(0, resVacio.getTutoriasProcesadas());
        assertEquals(0.00, resVacio.getTotalNeto(), 0.001);
        assertEquals("SIN_ACTIVIDAD", resVacio.getDetalleLiquidacion());

        // Prueba de docente nulo
        assertThrows(IllegalArgumentException.class, () ->
                servicioLiquidacion.procesarLiquidacionDocente(null, Collections.emptyList()));
    }
}
