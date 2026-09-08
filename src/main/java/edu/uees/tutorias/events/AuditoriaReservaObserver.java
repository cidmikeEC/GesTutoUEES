package edu.uees.tutorias.events;

import edu.uees.tutorias.domain.Reserva;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Observador concreto que registra una bitácora inmutable de auditoría
 * institucional de todas las operaciones realizadas sobre las reservas.
 */
public class AuditoriaReservaObserver implements ReservaObserver {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final List<String> bitacora = new ArrayList<>();

    @Override
    public void onEvento(EventoReserva evento) {
        Reserva r = evento.getReserva();
        String linea = String.format("[AUDITORÍA UEES | %s] Acción: %s | Reserva: %s | Alumno: %s (ID: %s) | Docente: %s | Materia: %s | Modalidad: %s | Detalle: %s",
                evento.getTimestamp().format(FORMATO),
                evento.getTipo(),
                r.getId(),
                r.getEstudiante().getNombre(),
                r.getEstudiante().getId(),
                r.getHorario().getDocente().getNombre(),
                r.getMateria(),
                r.getModalidad(),
                evento.getDetalle());
        bitacora.add(linea);
        System.out.println(linea);
    }

    public List<String> getBitacora() {
        return Collections.unmodifiableList(new ArrayList<>(bitacora));
    }

    public int getCantidadRegistros() {
        return bitacora.size();
    }

    public void limpiar() {
        bitacora.clear();
    }
}
