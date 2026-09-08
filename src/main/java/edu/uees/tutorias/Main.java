package edu.uees.tutorias;

import edu.uees.tutorias.adapter.ProveedorVideoconferencia;
import edu.uees.tutorias.adapter.ReunionVirtual;
import edu.uees.tutorias.adapter.ZoomServiceAdapter;
import edu.uees.tutorias.adapter.external.ZoomSdkClient;
import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.events.AuditoriaReservaObserver;
import edu.uees.tutorias.events.GestorEventosReserva;
import edu.uees.tutorias.events.NotificacionReservaObserver;
import edu.uees.tutorias.events.SincronizacionCalendarioObserver;
import edu.uees.tutorias.facade.AgendamientoTutoriaFacade;
import edu.uees.tutorias.notification.CreadorNotificador;
import edu.uees.tutorias.notification.CreadorNotificadorCorreo;
import edu.uees.tutorias.notification.CreadorNotificadorSMS;
import edu.uees.tutorias.notification.CreadorNotificadorTeams;
import edu.uees.tutorias.notification.CreadorNotificadorWhatsApp;
import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.persistence.RepositorioReservas;
import edu.uees.tutorias.persistence.RepositorioReservasEnMemoria;
import edu.uees.tutorias.service.ServicioReservas;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Demostración interactiva por consola del Sistema GesTutoUEES.
 *
 * Muestra la evolución completa de la arquitectura:
 * - INCREMENTO 0 (Ae1/Ae2): Factory Method (notificadores) y Builder (reservas inmutables).
 * - INCREMENTO 1 (Ae3):
 *     1. ADAPTER: Integración con API externa incompatible (Zoom Video Communications).
 *     2. OBSERVER: Gestión reactiva de ciclo de vida (Notificaciones + Auditoría + Calendario).
 *     3. FACADE: Orquestación y simplificación del subsistema para clientes externos.
 *
 * GesTutoUEES · Semana 4 | Actividad Ae3
 */
public class Main {

    public static void main(String[] args) {
        imprimirEncabezado("GesTutoUEES · Demostración del Incremento 1 (Ae3) y Patrones Integrados");

        // 1. Actores del dominio
        Docente docente1 = new Docente("DOC-01", "Lessette Zambrano Zurita",
                "lzambrano@uees.edu.ec", "Estructura de Datos");
        Docente docente2 = new Docente("DOC-02", "Jaime Sayago Heredia",
                "jsayago@uees.edu.ec", "Diseño de Software");

        Estudiante estudiante1 = new Estudiante("EST-01", "Miguel Iván Delgado",
                "mdelgado@uees.edu.ec", "Ingeniería en Computación");
        Estudiante estudiante2 = new Estudiante("EST-02", "Rubí Floreano",
                "rfloreano@uees.edu.ec", "Ingeniería en Computación");

        System.out.println("👤 Docentes registrados:");
        System.out.println("   • " + docente1.describirRol());
        System.out.println("   • " + docente2.describirRol());
        System.out.println("\n🎓 Estudiantes:");
        System.out.println("   • " + estudiante1.describirRol());
        System.out.println("   • " + estudiante2.describirRol());

        // 2. Horarios docentes disponibles
        Horario horario1 = new Horario("HOR-01", docente1,
                LocalDateTime.of(2026, 9, 10, 10, 0),
                LocalDateTime.of(2026, 9, 10, 11, 0));
        Horario horario2 = new Horario("HOR-02", docente2,
                LocalDateTime.of(2026, 9, 11, 15, 0),
                LocalDateTime.of(2026, 9, 11, 16, 30));
        Horario horario3 = new Horario("HOR-03", docente2,
                LocalDateTime.of(2026, 9, 12, 17, 0),
                LocalDateTime.of(2026, 9, 12, 18, 0));

        // =========================================================================
        // PARTE 1: PATRONES CREACIONALES HEREDADOS (Ae2)
        // =========================================================================
        imprimirSeccion("PARTE 1: BASE PREVIA (Builder y Factory Method bajo OCP)");

        Reserva reservaBuilder = Reserva.builder()
                .conId("RES-AE2-01")
                .paraEstudiante(estudiante1)
                .enHorario(horario1)
                .conMateria("Estructura de Datos")
                .conTema("Árboles Binarios de Búsqueda y Recursión")
                .modalidadVirtual()
                .conObservaciones("Revisar caso base de balanceo AVL")
                .build();
        System.out.println(">> [BUILDER] Reserva construida fluidamente:\n   " + reservaBuilder);

        CreadorNotificador creadorTeams = new CreadorNotificadorTeams();
        Notificador notificadorTeams = creadorTeams.crearNotificador();
        System.out.println(">> [FACTORY METHOD] Notificador polimórfico creado: " + notificadorTeams.getCanal());

        // =========================================================================
        // PARTE 2: PATRÓN ADAPTER (Integración de Zoom Video Communications)
        // =========================================================================
        imprimirSeccion("PARTE 2: PATRÓN ADAPTER (Integración de API Externa Incompatible)");

        System.out.println(">> Situación: La UEES contrata Zoom Cloud para sesiones virtuales.");
        System.out.println("   El SDK externo (ZoomSdkClient) tiene firmas incompatibles en inglés");
        System.out.println("   y tipos numéricos ajenos a nuestro dominio.");
        System.out.println("   El ZoomServiceAdapter implementa la interfaz limpia ProveedorVideoconferencia:\n");

        ZoomSdkClient zoomSdk = new ZoomSdkClient("uees-key-prod", "uees-secret-prod");
        ProveedorVideoconferencia proveedorZoom = new ZoomServiceAdapter(zoomSdk);

        ReunionVirtual salaZoom = proveedorZoom.crearSala(reservaBuilder);
        System.out.println("\n   [ADAPTER OK] Sala adaptada al dominio:");
        System.out.println("   • Plataforma : " + salaZoom.getPlataforma());
        System.out.println("   • ID normal  : " + salaZoom.getIdReunion());
        System.out.println("   • URL Acceso : " + salaZoom.getUrlAcceso());
        System.out.println("   • Clave      : " + salaZoom.getClaveAcceso());

        // =========================================================================
        // PARTE 3: PATRÓN OBSERVER (Gestión reactiva y desacoplada de eventos)
        // =========================================================================
        imprimirSeccion("PARTE 3: PATRÓN OBSERVER (Ciclo de Vida Reactivo de Reservas)");

        System.out.println(">> Situación: Al cambiar el estado de una reserva, múltiples receptores");
        System.out.println("   deben actuar de forma independiente sin acoplar el servicio:\n");

        GestorEventosReserva gestorEventos = new GestorEventosReserva();
        AuditoriaReservaObserver auditoria = new AuditoriaReservaObserver();
        SincronizacionCalendarioObserver calendario = new SincronizacionCalendarioObserver();
        NotificacionReservaObserver notifObserver = new NotificacionReservaObserver(notificadorTeams);

        gestorEventos.suscribir(auditoria);
        gestorEventos.suscribir(calendario);
        gestorEventos.suscribir(notifObserver);

        System.out.printf("   [OBSERVER OK] %d observadores suscritos: %s, %s, %s%n%n",
                gestorEventos.getCantidadObservadores(),
                auditoria.getNombre(),
                calendario.getNombre(),
                notifObserver.getNombre());

        RepositorioReservas repo = new RepositorioReservasEnMemoria();
        ServicioReservas servicioReservas = new ServicioReservas(repo, gestorEventos);

        // =========================================================================
        // PARTE 4: PATRÓN FACADE (Fachada unificada de agendamiento de tutorías)
        // =========================================================================
        imprimirSeccion("PARTE 4: PATRÓN FACADE (Orquestación Unificada de Alto Nivel)");

        System.out.println(">> Situación: El cliente solo invoca una operación concisa.");
        System.out.println("   La fachada coordina disponibilidad, Zoom Adapter, ReservaBuilder,");
        System.out.println("   persistencia y disparo de eventos Observer:\n");

        AgendamientoTutoriaFacade facade = new AgendamientoTutoriaFacade(servicioReservas, proveedorZoom);

        System.out.println(">>> 1. Agendando Tutoría Virtual mediante FACHADA...");
        Reserva tutoriaVirtual = facade.agendarTutoriaVirtual(
                estudiante1,
                horario2,
                "Diseño de Software",
                "Integración de Patrones Estructurales y de Comportamiento",
                20
        );
        System.out.println("    => Reserva confirmada ID: " + tutoriaVirtual.getId());
        System.out.println("    => Enlace asignado: " + tutoriaVirtual.getEnlaceVirtual());

        System.out.println("\n>>> 2. Agendando Tutoría Presencial mediante FACHADA...");
        Reserva tutoriaPresencial = facade.agendarTutoriaPresencial(
                estudiante2,
                horario3,
                "Diseño de Software",
                "Revisión de Arquitectura de Software y SOLID",
                "Edificio F - Laboratorio LAB-201"
        );
        System.out.println("    => Reserva confirmada ID: " + tutoriaPresencial.getId());
        System.out.println("    => Aula asignada: " + tutoriaPresencial.getAula());

        System.out.println("\n>>> 3. Cancelando una tutoría mediante FACHADA...");
        facade.cancelarTutoria(tutoriaVirtual.getId());

        // =========================================================================
        // RESUMEN DE LA BITÁCORA DE AUDITORÍA
        // =========================================================================
        imprimirSeccion("RESUMEN DE BITÁCORA INMUTABLE REGISTRADA POR EL OBSERVER");
        System.out.printf("Total de registros capturados en auditoría: %d%n", auditoria.getCantidadRegistros());
        for (String log : auditoria.getBitacora()) {
            System.out.println("  " + log);
        }

        imprimirEncabezado("INCREMENTO 1 EJECUTADO EXITOSAMENTE — UEES 2026");
    }

    private static void imprimirEncabezado(String titulo) {
        String linea = "═".repeat(80);
        System.out.println("\n" + linea);
        System.out.println("  " + titulo);
        System.out.println(linea + "\n");
    }

    private static void imprimirSeccion(String titulo) {
        String linea = "─".repeat(80);
        System.out.println("\n" + linea);
        System.out.println("  " + titulo);
        System.out.println(linea + "\n");
    }
}