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
import edu.uees.tutorias.persistence.RepositorioReservas;
import edu.uees.tutorias.persistence.RepositorioReservasEnMemoria;
import edu.uees.tutorias.service.ServicioReservas;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Demostración interactiva por consola de los Patrones Creacionales:
 * 1. FACTORY METHOD: Creación polimórfica y extensible de mecanismos de notificación.
 * 2. BUILDER: Construcción fluida, paso a paso y validada de instancias complejas de Reserva.
 *
 * GesTutoUEES · Semana 3 | Actividad Ae2
 */
public class Main {

    public static void main(String[] args) {
        imprimirEncabezado("GesTutoUEES · Demostración de Patrones Creacionales (Ae2)");

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
                LocalDateTime.of(2026, 9, 3, 10, 0),
                LocalDateTime.of(2026, 9, 3, 11, 0));
        Horario horario2 = new Horario("HOR-02", docente2,
                LocalDateTime.of(2026, 9, 4, 15, 0),
                LocalDateTime.of(2026, 9, 4, 16, 30));

        System.out.println("\n📅 Horarios publicados:");
        System.out.println("   • " + horario1);
        System.out.println("   • " + horario2);

        // =========================================================================
        // PARTE 1: DEMOSTRACIÓN PATRÓN BUILDER (Construcción fluida de Reservas)
        // =========================================================================
        imprimirSeccion("PARTE 1: PATRÓN BUILDER (Construcción fluida con Fluent API)");

        System.out.println(">> Caso 1: Reserva Virtual Individual (aplicando valores por defecto)");
        Reserva reservaVirtual = Reserva.builder()
                .conId("RES-2026-001")
                .paraEstudiante(estudiante1)
                .enHorario(horario1)
                .conMateria("Estructura de Datos")
                .conTema("Árboles Binarios de Búsqueda y Recursión")
                .modalidadVirtual() // Aplica default Teams link, recordatorio 30m, individual
                .conObservaciones("Revisar caso base de balanceo AVL")
                .build();
        System.out.println("   [OK] Creada con éxito:\n   " + reservaVirtual + "\n");

        System.out.println(">> Caso 2: Reserva Presencial Grupal con configuración personalizada");
        Reserva reservaPresencial = Reserva.builder()
                .conId("RES-2026-002")
                .paraEstudiante(estudiante2)
                .enHorario(horario2)
                .conMateria("Diseño de Software")
                .conTema("Patrones GoF (Factory Method vs Builder)")
                .modalidadPresencial("Laboratorio de Software LAB-304")
                .grupal(6)
                .conRecordatorioMinutos(15)
                .conObservaciones("Traer diagramas PlantUML listos")
                .build();
        System.out.println("   [OK] Creada con éxito:\n   " + reservaPresencial + "\n");

        System.out.println(">> Caso 3: Prueba de validación de negocio en el Builder (debe fallar)");
        try {
            Reserva.builder()
                    .conId("RES-INVALIDA")
                    .paraEstudiante(estudiante1)
                    .enHorario(horario1)
                    .conMateria("Diseño")
                    .conTema("Prueba")
                    .modalidadPresencial("") // Aula vacía en modalidad presencial -> Regla violada
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("   [VALIDACIÓN EXITOSA] Constructor rechazó datos inconsistentes: " + e.getMessage());
        }

        // =========================================================================
        // PARTE 2: DEMOSTRACIÓN PATRÓN FACTORY METHOD (Notificaciones polimórficas)
        // =========================================================================
        imprimirSeccion("PARTE 2: PATRÓN FACTORY METHOD (Despacho multicanal extensible)");

        // Lista de fábricas concretas (ConcreteCreators)
        List<CreadorNotificador> creadores = List.of(
                new CreadorNotificadorCorreo(),
                new CreadorNotificadorSMS(),
                new CreadorNotificadorWhatsApp(),
                new CreadorNotificadorTeams() // Variante extensible
        );

        System.out.println(">> Enviando notificación de Reserva 1 por todos los canales disponibles:");
        for (CreadorNotificador creador : creadores) {
            creador.enviarNotificacionReserva(reservaVirtual);
        }

        // =========================================================================
        // PARTE 3: INTEGRACIÓN CON EL SERVICIO DE DOMINIO (GesTutoUEES)
        // =========================================================================
        imprimirSeccion("PARTE 3: INTEGRACIÓN COMPLETA CON SERVICIO DE DOMINIO");

        RepositorioReservas repositorio = new RepositorioReservasEnMemoria();
        // Inyectamos la fábrica/notificador Teams al servicio de reservas
        CreadorNotificador fabricaElegida = new CreadorNotificadorTeams();
        ServicioReservas servicio = new ServicioReservas(repositorio, fabricaElegida.crearNotificador());

        System.out.println(">> Registrando Reserva en Servicio con Notificador Teams inyectado...");
        servicio.registrarReserva(reservaVirtual);
        System.out.println(">> Estado actual del horario docente: " + horario1 + "\n");

        System.out.println(">> Demostración de Cancelación y liberación automática de cupo:");
        servicio.cancelar(reservaVirtual.getId());
        System.out.println(">> Estado del horario tras cancelar: " + horario1 + "\n");

        imprimirEncabezado("Demostración finalizada exitosamente.");
    }

    private static void imprimirEncabezado(String titulo) {
        System.out.println("\n" + "=".repeat(75));
        System.out.println("  " + titulo);
        System.out.println("=".repeat(75) + "\n");
    }

    private static void imprimirSeccion(String subtitulo) {
        System.out.println("\n" + "-".repeat(75));
        System.out.println("  " + subtitulo);
        System.out.println("-".repeat(75) + "\n");
    }
}