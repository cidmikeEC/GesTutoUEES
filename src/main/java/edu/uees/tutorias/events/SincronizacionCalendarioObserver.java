package edu.uees.tutorias.events;

import edu.uees.tutorias.domain.Reserva;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Observador concreto que simula la sincronización automática de calendarios
 * institucionales (Exchange / Microsoft 365 / Google Calendar) ante eventos de reserva.
 */
public class SincronizacionCalendarioObserver implements ReservaObserver {

    private final List<String> eventosSincronizados = new ArrayList<>();

    @Override
    public void onEvento(EventoReserva evento) {
        Reserva r = evento.getReserva();
        String mensaje;
        switch (evento.getTipo()) {
            case CREADA:
            case CONFIRMADA:
                mensaje = String.format("[CALENDARIO 365] Evento agendado en buzón docente (%s) y alumno (%s) para fecha: %s",
                        r.getHorario().getDocente().getCorreo(),
                        r.getEstudiante().getCorreo(),
                        r.getHorario().getInicio());
                break;
            case CANCELADA:
                mensaje = String.format("[CALENDARIO 365] Evento eliminado del calendario de %s y %s (Reserva: %s)",
                        r.getHorario().getDocente().getCorreo(),
                        r.getEstudiante().getCorreo(),
                        r.getId());
                break;
            default:
                mensaje = String.format("[CALENDARIO 365] Actualización procesada para reserva %s", r.getId());
                break;
        }
        eventosSincronizados.add(mensaje);
        System.out.println(mensaje);
    }

    public List<String> getEventosSincronizados() {
        return Collections.unmodifiableList(new ArrayList<>(eventosSincronizados));
    }

    public int getCantidadSincronizaciones() {
        return eventosSincronizados.size();
    }
}
