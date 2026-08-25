package edu.uees.tutorias.domain;

/**
 * Un estudiante solicita y reserva tutorías.
 * Hereda de Usuario: ES una persona del sistema (herencia válida,
 * no solo para reutilizar código).
 */
public class Estudiante extends Usuario {

    private String carrera;

    public Estudiante(String id, String nombre, String correo, String carrera) {
        super(id, nombre, correo);
        this.carrera = carrera;
    }

    public String getCarrera() {
        return carrera;
    }

    @Override
    public String describirRol() {
        return "Estudiante de " + carrera + " que solicita tutorías";
    }
}