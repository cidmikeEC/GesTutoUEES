package edu.uees.tutorias;

import edu.uees.tutorias.adapter.ProveedorVideoconferencia;
import edu.uees.tutorias.adapter.ReunionVirtual;
import edu.uees.tutorias.adapter.ZoomServiceAdapter;
import edu.uees.tutorias.adapter.external.ZoomSdkClient;
import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas del Patrón Adapter (Integración con API externa de Zoom)")
class AdapterTest {

    private ZoomSdkClient zoomSdk;
    private ProveedorVideoconferencia adapter;
    private Reserva reservaVirtual;

    @BeforeEach
    void setUp() {
        zoomSdk = new ZoomSdkClient("api-key-uees-test", "api-secret-uees-test");
        adapter = new ZoomServiceAdapter(zoomSdk);

        Docente docente = new Docente("DOC-ADP", "Lessette Zambrano", "lzambrano@uees.edu.ec", "Estructura de Datos");
        Estudiante estudiante = new Estudiante("EST-ADP", "Miguel Delgado", "mdelgado@uees.edu.ec", "Computación");
        Horario horario = new Horario("HOR-ADP", docente,
                LocalDateTime.of(2026, 9, 20, 10, 0),
                LocalDateTime.of(2026, 9, 20, 11, 0));

        reservaVirtual = Reserva.builder()
                .conId("RES-ADAPTER-01")
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria("Diseño de Software")
                .conTema("Patrón Adapter e Integración")
                .modalidadVirtual()
                .build();
    }

    @Test
    @DisplayName("El adaptador traduce la Reserva al contrato del SDK de Zoom y retorna ReunionVirtual")
    void testCrearSalaMedianteAdaptador() {
        assertEquals(0, zoomSdk.getCantidadSalasActivas());

        ReunionVirtual reunion = adapter.crearSala(reservaVirtual);

        assertNotNull(reunion);
        assertNotNull(reunion.getIdReunion());
        assertTrue(reunion.getUrlAcceso().startsWith("https://zoom.us/j/"));
        assertNotNull(reunion.getClaveAcceso());
        assertEquals("Zoom Video Communications (UEES Enterprise)", reunion.getPlataforma());
        assertEquals(60, reunion.getDuracionMinutos());

        // Verificar que en el SDK externo ahora existe 1 sala activa
        assertEquals(1, zoomSdk.getCantidadSalasActivas());
    }

    @Test
    @DisplayName("El adaptador cancela una sala existente a través del SDK externo")
    void testCancelarSalaMedianteAdaptador() {
        ReunionVirtual reunion = adapter.crearSala(reservaVirtual);
        assertEquals(1, zoomSdk.getCantidadSalasActivas());

        boolean cancelada = adapter.cancelarSala(reunion.getIdReunion());
        assertTrue(cancelada);
        assertEquals(0, zoomSdk.getCantidadSalasActivas());
    }

    @Test
    @DisplayName("Cancelar sala con ID no numérico retorna false y no lanza excepción hacia el cliente")
    void testCancelarSalaIdInvalido() {
        boolean cancelada = adapter.cancelarSala("id-no-numerico-invalido");
        assertFalse(cancelada);
    }
}
