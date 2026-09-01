package edu.uees.tutorias.domain;

import java.time.LocalDateTime;

/**
 * Representa el encuentro confirmado entre un estudiante y un docente en un horario específico.
 * Es un objeto inmutable en sus datos de configuración, construido mediante el patrón Builder.
 * Protege su propio ciclo de vida (confirmar, cancelar, reprogramar).
 */
public class Reserva {

    // Atributos obligatorios
    private final String id;
    private final Estudiante estudiante;
    private final Horario horario;
    private final String materia;
    private final String tema;

    // Atributos opcionales / configurables
    private final ModalidadTutoria modalidad;
    private final String enlaceVirtual;
    private final String aula;
    private final int recordatorioMinutos;
    private final boolean esGrupal;
    private final int cupoMaximo;
    private final String observaciones;
    private final LocalDateTime creadaEn;

    // Estado mutable del ciclo de vida
    private EstadoReserva estado;

    /**
     * Constructor utilizado por ReservaBuilder.
     */
    Reserva(ReservaBuilder builder) {
        this.id = builder.id;
        this.estudiante = builder.estudiante;
        this.horario = builder.horario;
        this.materia = builder.materia;
        this.tema = builder.tema;
        this.modalidad = builder.modalidad;
        this.enlaceVirtual = builder.enlaceVirtual;
        this.aula = builder.aula;
        this.recordatorioMinutos = builder.recordatorioMinutos;
        this.esGrupal = builder.esGrupal;
        this.cupoMaximo = builder.cupoMaximo;
        this.observaciones = builder.observaciones;
        this.creadaEn = builder.creadaEn != null ? builder.creadaEn : LocalDateTime.now();
        this.estado = builder.estadoInicial != null ? builder.estadoInicial : EstadoReserva.PENDIENTE;
    }

    /**
     * Constructor de compatibilidad para escenarios básicos.
     */
    public Reserva(String id, Estudiante estudiante, Horario horario) {
        this(new ReservaBuilder()
                .conId(id)
                .paraEstudiante(estudiante)
                .enHorario(horario)
                .conMateria(horario.getDocente().getEspecialidad())
                .conTema("Consulta general académica"));
    }

    public static ReservaBuilder builder() {
        return new ReservaBuilder();
    }

    public String getId() {
        return id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Horario getHorario() {
        return horario;
    }

    public String getMateria() {
        return materia;
    }

    public String getTema() {
        return tema;
    }

    public ModalidadTutoria getModalidad() {
        return modalidad;
    }

    public String getEnlaceVirtual() {
        return enlaceVirtual;
    }

    public String getAula() {
        return aula;
    }

    public int getRecordatorioMinutos() {
        return recordatorioMinutos;
    }

    public boolean isEsGrupal() {
        return esGrupal;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public LocalDateTime getCreadaEn() {
        return creadaEn;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    /** Confirma la reserva: solo si está pendiente. */
    public void confirmar() {
        if (estado != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException("Solo se confirma una reserva pendiente");
        }
        this.estado = EstadoReserva.CONFIRMADA;
    }

    /** Cancela la reserva y libera el horario para que otro lo tome. */
    public void cancelar() {
        if (estado == EstadoReserva.CANCELADA || estado == EstadoReserva.REALIZADA) {
            throw new IllegalStateException("No se puede cancelar una reserva " + estado);
        }
        this.estado = EstadoReserva.CANCELADA;
        this.horario.liberar();
    }

    /** Reprograma la reserva hacia un horario nuevo. */
    public void reprogramar() {
        if (estado != EstadoReserva.CONFIRMADA && estado != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException("Solo se reprograma una reserva vigente");
        }
        this.estado = EstadoReserva.REPROGRAMADA;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Reserva ").append(id)
          .append(" | Alumno: ").append(estudiante.getNombre())
          .append(" | Docente: ").append(horario.getDocente().getNombre())
          .append(" | Materia: ").append(materia)
          .append(" | Tema: ").append(tema)
          .append(" | Modalidad: ").append(modalidad);
        if (modalidad == ModalidadTutoria.VIRTUAL) {
            sb.append(" (").append(enlaceVirtual).append(")");
        } else {
            sb.append(" (Aula: ").append(aula).append(")");
        }
        sb.append(" | ").append(esGrupal ? "Grupal (Cupos: " + cupoMaximo + ")" : "Individual")
          .append(" | Recordatorio: ").append(recordatorioMinutos).append("m")
          .append(" [").append(estado).append("]");
        return sb.toString();
    }
}