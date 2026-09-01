package edu.uees.tutorias.domain;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Builder para la creación paso a paso de instancias de {@link Reserva}.
 * Proporciona una Fluent API, asignación de valores por defecto sensatos
 * y validaciones estrictas de consistencia de negocio antes de la construcción.
 */
public class ReservaBuilder {

    // Campos obligatorios
    String id;
    Estudiante estudiante;
    Horario horario;
    String materia;
    String tema;

    // Campos opcionales con valores por defecto
    ModalidadTutoria modalidad = ModalidadTutoria.VIRTUAL;
    String enlaceVirtual;
    String aula;
    int recordatorioMinutos = 30;
    boolean esGrupal = false;
    int cupoMaximo = 1;
    String observaciones = "";
    LocalDateTime creadaEn;
    EstadoReserva estadoInicial = EstadoReserva.PENDIENTE;

    public ReservaBuilder() {
        // Generación de ID por defecto si no se suministra explícitamente
        this.id = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.creadaEn = LocalDateTime.now();
    }

    public ReservaBuilder conId(String id) {
        this.id = id;
        return this;
    }

    public ReservaBuilder paraEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
        return this;
    }

    public ReservaBuilder enHorario(Horario horario) {
        this.horario = horario;
        return this;
    }

    public ReservaBuilder conMateria(String materia) {
        this.materia = materia;
        return this;
    }

    public ReservaBuilder conTema(String tema) {
        this.tema = tema;
        return this;
    }

    public ReservaBuilder modalidadVirtual() {
        this.modalidad = ModalidadTutoria.VIRTUAL;
        return this;
    }

    public ReservaBuilder modalidadVirtual(String enlaceVirtual) {
        this.modalidad = ModalidadTutoria.VIRTUAL;
        this.enlaceVirtual = enlaceVirtual;
        return this;
    }

    public ReservaBuilder modalidadPresencial(String aula) {
        this.modalidad = ModalidadTutoria.PRESENCIAL;
        this.aula = aula;
        return this;
    }

    public ReservaBuilder conRecordatorioMinutos(int recordatorioMinutos) {
        this.recordatorioMinutos = recordatorioMinutos;
        return this;
    }

    public ReservaBuilder grupal(int cupoMaximo) {
        this.esGrupal = true;
        this.cupoMaximo = cupoMaximo;
        return this;
    }

    public ReservaBuilder individual() {
        this.esGrupal = false;
        this.cupoMaximo = 1;
        return this;
    }

    public ReservaBuilder conObservaciones(String observaciones) {
        this.observaciones = observaciones;
        return this;
    }

    public ReservaBuilder conEstadoInicial(EstadoReserva estadoInicial) {
        this.estadoInicial = estadoInicial;
        return this;
    }

    public ReservaBuilder creadaEn(LocalDateTime creadaEn) {
        this.creadaEn = creadaEn;
        return this;
    }

    /**
     * Valida la consistencia de los datos y construye la instancia de Reserva.
     * @return Una instancia inmutable y válida de Reserva.
     * @throws IllegalStateException si faltan campos obligatorios o hay inconsistencias.
     */
    public Reserva build() {
        validarCamposObligatorios();
        validarReglasDeNegocio();
        return new Reserva(this);
    }

    private void validarCamposObligatorios() {
        if (id == null || id.isBlank()) {
            throw new IllegalStateException("El ID de la reserva es obligatorio.");
        }
        if (estudiante == null) {
            throw new IllegalStateException("La reserva debe tener un Estudiante asociado.");
        }
        if (horario == null) {
            throw new IllegalStateException("La reserva debe tener un Horario asociado.");
        }
        if (materia == null || materia.isBlank()) {
            throw new IllegalStateException("Debe especificarse la materia de la tutoría.");
        }
        if (tema == null || tema.isBlank()) {
            throw new IllegalStateException("Debe especificarse el tema o motivo de la tutoría.");
        }
    }

    private void validarReglasDeNegocio() {
        if (recordatorioMinutos < 0) {
            throw new IllegalStateException("El tiempo de recordatorio no puede ser negativo.");
        }

        if (modalidad == ModalidadTutoria.VIRTUAL) {
            if (enlaceVirtual == null || enlaceVirtual.isBlank()) {
                // Generación de enlace de reunión seguro por defecto
                this.enlaceVirtual = "https://teams.microsoft.com/l/meetup-join/uees-tutoria-" + id.toLowerCase();
            }
        } else if (modalidad == ModalidadTutoria.PRESENCIAL) {
            if (aula == null || aula.isBlank()) {
                throw new IllegalStateException("Para tutorías presenciales se debe especificar el aula o laboratorio.");
            }
        }

        if (esGrupal) {
            if (cupoMaximo <= 1) {
                throw new IllegalStateException("Una tutoría grupal debe permitir al menos 2 estudiantes.");
            }
        } else {
            if (cupoMaximo != 1) {
                throw new IllegalStateException("Una tutoría individual debe tener un cupo máximo de 1.");
            }
        }
    }
}
