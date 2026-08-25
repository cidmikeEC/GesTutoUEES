package edu.uees.tutorias;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.notification.NotificadorCorreo;
import edu.uees.tutorias.persistence.RepositorioReservas;
import edu.uees.tutorias.persistence.RepositorioReservasEnMemoria;
import edu.uees.tutorias.service.ServicioReservas;
import java.time.LocalDateTime;

/**
 * Demostración por consola del flujo completo de una tutoría.
 * Aquí se "cablean" las implementaciones concretas (el único lugar que las
 * conoce) y se inyectan en el servicio. El dominio queda limpio.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== GesTutoUEES · Demostración del sistema de tutorías ===\n");

        // 1. Composición de la aplicación (se eligen las implementaciones).
        RepositorioReservas repositorio = new RepositorioReservasEnMemoria();
        Notificador notificador = new NotificadorCorreo();
        ServicioReservas servicio = new ServicioReservas(repositorio, notificador);

        // 2. Actores del dominio.
        Docente docente = new Docente("DOC-01", "Lessette Zambrano",
                "lzambrano@uees.edu.ec", "Estructura de Datos");
        Estudiante estudiante = new Estudiante("EST-01", "Miguel Delgado",
                "mdelgado@uees.edu.ec", "Computación");
        System.out.println(docente.describirRol());
        System.out.println(estudiante.describirRol() + "\n");

        // 3. El docente publica un horario disponible.
        Horario horario = new Horario("HOR-01", docente,
                LocalDateTime.of(2026, 8, 26, 10, 0),
                LocalDateTime.of(2026, 8, 26, 11, 0));
        System.out.println("Horario publicado: " + horario + "\n");

        // 4. El estudiante reserva el horario.
        System.out.println(">> El estudiante reserva el horario...");
        Reserva reserva = servicio.reservar(estudiante, horario);
        System.out.println("  Creada: " + reserva);
        System.out.println("  Estado del horario: " + horario + "\n");

        // 5. Intento de doble reserva sobre el mismo horario (debe fallar).
        System.out.println(">> Otro estudiante intenta reservar el MISMO horario...");
        Estudiante otro = new Estudiante("EST-02", "Rubí Floreano",
                "rfloreano@uees.edu.ec", "Computación");
        try {
            servicio.reservar(otro, horario);
        } catch (IllegalStateException e) {
            System.out.println("  Rechazado (regla anti doble reserva): " + e.getMessage() + "\n");
        }

        // 6. El estudiante cancela y el horario vuelve a quedar libre.
        System.out.println(">> El estudiante cancela su reserva...");
        servicio.cancelar(reserva.getId());
        System.out.println("  Estado del horario tras cancelar: " + horario + "\n");

        // 7. Resumen final.
        System.out.println("=== Reservas registradas en el sistema ===");
        servicio.listarReservas().forEach(r -> System.out.println("  " + r));
    }
}