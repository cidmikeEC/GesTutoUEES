package edu.uees.tutorias.service;

import java.util.Objects;

/**
 * DTO que encapsula el resultado observable de la liquidación de tutorías de un docente.
 * Este objeto define el contrato de comportamiento observable que debe preservarse intacto
 * durante todo el proceso de refactorización de la Kata (Ae4).
 */
public class LiquidacionDocenteDTO {

    private final String idDocente;
    private final String nombreDocente;
    private final int tutoriasProcesadas;
    private final double totalHonorarios;
    private final double totalPenalizaciones;
    private final double totalNeto;
    private final String detalleLiquidacion;

    public LiquidacionDocenteDTO(String idDocente, String nombreDocente, int tutoriasProcesadas,
                                 double totalHonorarios, double totalPenalizaciones,
                                 double totalNeto, String detalleLiquidacion) {
        this.idDocente = idDocente;
        this.nombreDocente = nombreDocente;
        this.tutoriasProcesadas = tutoriasProcesadas;
        this.totalHonorarios = totalHonorarios;
        this.totalPenalizaciones = totalPenalizaciones;
        this.totalNeto = totalNeto;
        this.detalleLiquidacion = detalleLiquidacion;
    }

    public String getIdDocente() {
        return idDocente;
    }

    public String getNombreDocente() {
        return nombreDocente;
    }

    public int getTutoriasProcesadas() {
        return tutoriasProcesadas;
    }

    public double getTotalHonorarios() {
        return totalHonorarios;
    }

    public double getTotalPenalizaciones() {
        return totalPenalizaciones;
    }

    public double getTotalNeto() {
        return totalNeto;
    }

    public String getDetalleLiquidacion() {
        return detalleLiquidacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiquidacionDocenteDTO that = (LiquidacionDocenteDTO) o;
        return tutoriasProcesadas == that.tutoriasProcesadas &&
                Double.compare(that.totalHonorarios, totalHonorarios) == 0 &&
                Double.compare(that.totalPenalizaciones, totalPenalizaciones) == 0 &&
                Double.compare(that.totalNeto, totalNeto) == 0 &&
                Objects.equals(idDocente, that.idDocente) &&
                Objects.equals(nombreDocente, that.nombreDocente) &&
                Objects.equals(detalleLiquidacion, that.detalleLiquidacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDocente, nombreDocente, tutoriasProcesadas,
                totalHonorarios, totalPenalizaciones, totalNeto, detalleLiquidacion);
    }

    @Override
    public String toString() {
        return String.format("LiquidacionDocenteDTO[Docente: %s (%s) | Tutorías: %d | Honorarios: $%.2f | Penalizaciones: $%.2f | Neto: $%.2f]",
                nombreDocente, idDocente, tutoriasProcesadas, totalHonorarios, totalPenalizaciones, totalNeto);
    }
}
