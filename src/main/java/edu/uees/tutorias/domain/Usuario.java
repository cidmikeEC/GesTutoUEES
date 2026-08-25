package edu.uees.tutorias.domain;

/**
 * Clase base de cualquier persona que usa el sistema.
 * De aquí heredan Estudiante y Docente (generalización).
 * Es abstracta porque nadie se registra como "usuario genérico":
 * siempre es un estudiante o un docente.
 */
public abstract class Usuario {

    // Encapsulados: solo se cambian por métodos controlados, no directos.
    private final String id;
    private String nombre;
    private String correo;

    protected Usuario(String id, String nombre, String correo) {
        // Regla: un usuario sin identidad mínima no tiene sentido.
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id del usuario es obligatorio");
        }
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    /**
     * Cada tipo de usuario se describe distinto. El polimorfismo permite
     * tratar a todos como Usuario y que cada uno responda a su manera.
     */
    public abstract String describirRol();
}