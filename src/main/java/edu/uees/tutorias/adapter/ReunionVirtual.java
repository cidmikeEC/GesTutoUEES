package edu.uees.tutorias.adapter;

import java.time.LocalDateTime;

/**
 * Value Object del dominio GesTutoUEES que representa una sala de videoconferencia
 * abstracta e independiente de cualquier plataforma externa concreta.
 */
public class ReunionVirtual {

    private final String idReunion;
    private final String urlAcceso;
    private final String claveAcceso;
    private final String plataforma;
    private final LocalDateTime fechaHora;
    private final int duracionMinutos;

    public ReunionVirtual(String idReunion, String urlAcceso, String claveAcceso,
                          String plataforma, LocalDateTime fechaHora, int duracionMinutos) {
        this.idReunion = idReunion;
        this.urlAcceso = urlAcceso;
        this.claveAcceso = claveAcceso;
        this.plataforma = plataforma;
        this.fechaHora = fechaHora;
        this.duracionMinutos = duracionMinutos;
    }

    public String getIdReunion() {
        return idReunion;
    }

    public String getUrlAcceso() {
        return urlAcceso;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    @Override
    public String toString() {
        return String.format("ReunionVirtual[%s | ID: %s | URL: %s | Clave: %s]",
                plataforma, idReunion, urlAcceso, claveAcceso != null ? claveAcceso : "N/A");
    }
}
