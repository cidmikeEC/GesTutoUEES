package edu.uees.tutorias.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Sujeto observable (Subject / Publisher) en el patrón Observer.
 * Administra el ciclo de registro, desregistro y despacho de eventos
 * hacia los observadores suscritos de forma desacoplada.
 */
public class GestorEventosReserva {

    private final List<ReservaObserver> observadores = new CopyOnWriteArrayList<>();

    public void suscribir(ReservaObserver observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void desuscribir(ReservaObserver observador) {
        observadores.remove(observador);
    }

    public void notificar(EventoReserva evento) {
        for (ReservaObserver observador : observadores) {
            try {
                observador.onEvento(evento);
            } catch (Exception e) {
                System.err.printf("[GESTOR EVENTOS] Error notificando a %s: %s%n",
                        observador.getNombre(), e.getMessage());
            }
        }
    }

    public int getCantidadObservadores() {
        return observadores.size();
    }

    public List<ReservaObserver> getObservadores() {
        return Collections.unmodifiableList(new ArrayList<>(observadores));
    }

    public void limpiarObservadores() {
        observadores.clear();
    }
}
