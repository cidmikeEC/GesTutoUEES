package edu.uees.tutorias.domain;

/**
 * Un docente publica y administra sus horarios disponibles.
 * Hereda de Usuario (también ES una persona del sistema).
 */
public class Docente extends Usuario {

    private String especialidad;

    public Docente(String id, String nombre, String correo, String especialidad) {
        super(id, nombre, correo);
        this.especialidad = especialidad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    @Override
    public String describirRol() {
        return "Docente de " + especialidad + " que ofrece tutorías";
    }
}