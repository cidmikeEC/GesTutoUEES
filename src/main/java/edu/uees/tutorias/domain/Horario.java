package edu.uees.tutorias.domain;

import java.time.LocalDateTime;

/**
 * Un bloque de tiempo que un docente pone a disposición.
 * La disponibilidad se protege con métodos (no se cambia el estado a lo loco):
 * es la regla que evita la doble reserva.
 */
public class Horario {

    private final String id;
    private final Docente docente;
    private final LocalDateTime inicio;
    private final LocalDateTime fin;
    private boolean disponible;

    public Horario(String id, Docente docente, LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null || !fin.isAfter(inicio)) {
            throw new IllegalArgumentException("El rango del horario es inválido");
        }
        this.id = id;
        this.docente = docente;
        this.inicio = inicio;
        this.fin = fin;
        this.disponible = true; // todo horario nace libre
    }

    public String getId() {
        return id;
    }

    public Docente getDocente() {
        return docente;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public boolean estaDisponible() {
        return disponible;
    }

    /** Bloquea el horario al reservarse. Falla si ya estaba ocupado. */
    public void ocupar() {
        if (!disponible) {
            throw new IllegalStateException("El horario " + id + " ya está reservado");
        }
        this.disponible = false;
    }

    /** Libera el horario al cancelar o reprogramar. */
    public void liberar() {
        this.disponible = true;
    }

    @Override
    public String toString() {
        return "Horario " + id + " [" + inicio + " a " + fin + "] de "
                + docente.getNombre() + (disponible ? " (libre)" : " (ocupado)");
    }
}