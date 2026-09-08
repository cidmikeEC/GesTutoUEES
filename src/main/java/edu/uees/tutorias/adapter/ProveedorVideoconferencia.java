package edu.uees.tutorias.adapter;

import edu.uees.tutorias.domain.Reserva;

/**
 * Contrato del dominio (Target en el patrón Adapter).
 * Define la abstracción mediante la cual el sistema gestiona salas virtuales,
 * aislando el núcleo de la aplicación de APIs incompatibles o propietarias.
 */
public interface ProveedorVideoconferencia {

    /**
     * Crea y configura una sala de videoconferencia para una tutoría virtual.
     *
     * @param reserva Reserva confirmada o en proceso de reserva.
     * @return Objeto del dominio con la información normalizada de la reunión.
     */
    ReunionVirtual crearSala(Reserva reserva);

    /**
     * Cancela o desmantela una sala de reunión existente.
     *
     * @param idReunion Identificador normalizado de la reunión.
     * @return true si la sala fue cancelada exitosamente.
     */
    boolean cancelarSala(String idReunion);

    /**
     * Devuelve el nombre del proveedor externo adaptado.
     */
    String getNombreProveedor();
}
