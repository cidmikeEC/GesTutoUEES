package edu.uees.tutorias.persistence;

import edu.uees.tutorias.domain.Reserva;
import java.util.List;
import java.util.Optional;

/**
 * Abstracción de la persistencia de reservas.
 * El dominio no sabe si los datos van a memoria, a un archivo o a una base
 * de datos: solo habla con esta interfaz (Dependency Inversion). Así, cambiar
 * la tecnología de persistencia no toca la lógica de negocio.
 */
public interface RepositorioReservas {

    void guardar(Reserva reserva);

    Optional<Reserva> buscarPorId(String id);

    List<Reserva> listarTodas();
}