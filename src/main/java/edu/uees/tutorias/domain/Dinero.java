package edu.uees.tutorias.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;

/**
 * Value Object inmutable que modela un monto monetario en el dominio de tutorías.
 *
 * Refactorización 1 (Ae5): Erradicación del code smell 'Primitive Obsession'.
 * Encapsula la escala monetaria (2 decimales con redondeo HALF_UP), operaciones
 * aritméticas seguras y validación de invariantes de negocio.
 */
public final class Dinero implements Comparable<Dinero> {

    public static final Dinero CERO = new Dinero(BigDecimal.ZERO);

    private final BigDecimal monto;

    private Dinero(BigDecimal monto) {
        if (monto == null) {
            throw new IllegalArgumentException("El monto monetario no puede ser nulo");
        }
        if (monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto monetario no puede ser negativo: " + monto);
        }
        this.monto = monto.setScale(2, RoundingMode.HALF_UP);
    }

    public static Dinero de(double valor) {
        return new Dinero(BigDecimal.valueOf(valor));
    }

    public static Dinero cero() {
        return CERO;
    }

    public Dinero sumar(Dinero otro) {
        if (otro == null) {
            return this;
        }
        return new Dinero(this.monto.add(otro.monto));
    }

    public Dinero restar(Dinero otro) {
        if (otro == null) {
            return this;
        }
        BigDecimal resultado = this.monto.subtract(otro.monto);
        if (resultado.compareTo(BigDecimal.ZERO) < 0) {
            return CERO;
        }
        return new Dinero(resultado);
    }

    public Dinero multiplicarPor(double factor) {
        if (factor < 0) {
            throw new IllegalArgumentException("El factor de multiplicación no puede ser negativo");
        }
        return new Dinero(this.monto.multiply(BigDecimal.valueOf(factor)));
    }

    public Dinero porcentaje(double porcentaje) {
        return multiplicarPor(porcentaje);
    }

    public double getValor() {
        return this.monto.doubleValue();
    }

    public BigDecimal getMonto() {
        return this.monto;
    }

    public String formatear() {
        return String.format(Locale.US, "%.2f", getValor());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dinero dinero = (Dinero) o;
        return this.monto.compareTo(dinero.monto) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.monto.stripTrailingZeros());
    }

    @Override
    public int compareTo(Dinero o) {
        return this.monto.compareTo(o.monto);
    }

    @Override
    public String toString() {
        return "$" + formatear();
    }
}
