package edu.uees.tutorias.persistence;

import edu.uees.tutorias.domain.Reserva;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Persistencia en memoria (un HashMap). Sirve para esta etapa y para las
 * pruebas, sin levantar una base de datos. Cuando toque persistir de verdad,
 * se crea otra implementación de RepositorioReservas y el dominio no cambia.
 */
public class RepositorioReservasEnMemoria implements RepositorioReservas {

    private final List<Reserva> reservas = new ArrayList<>();

    @Override
    public void guardar(Reserva reserva) {
        reservas.add(reserva);
    }

    @Override
    public Optional<Reserva> buscarPorId(String id) {
        return reservas.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Reserva> listarTodas() {
        // Copia defensiva: nadie modifica la lista interna desde fuera.
        return new ArrayList<>(reservas);
    }
}