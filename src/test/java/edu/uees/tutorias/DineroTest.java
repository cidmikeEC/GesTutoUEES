package edu.uees.tutorias;

import edu.uees.tutorias.domain.Dinero;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Value Object Dinero · Refactorización 1 (Ae5)")
class DineroTest {

    @Test
    @DisplayName("Debe crear dinero con escala y redondeo a 2 decimales")
    void testCreacionYFormateo() {
        Dinero d = Dinero.de(32.50);
        assertEquals(32.50, d.getValor(), 0.001);
        assertEquals("32.50", d.formatear());
        assertEquals("$32.50", d.toString());
    }

    @Test
    @DisplayName("Debe sumar y restar montos correctamente")
    void testOperacionesAritmeticas() {
        Dinero base = Dinero.de(30.00);
        Dinero bono = Dinero.de(2.50);
        Dinero total = base.sumar(bono);

        assertEquals(32.50, total.getValor(), 0.001);

        Dinero resta = total.restar(Dinero.de(10.00));
        assertEquals(22.50, resta.getValor(), 0.001);
    }

    @Test
    @DisplayName("Debe calcular porcentajes y multiplicaciones")
    void testPorcentajes() {
        Dinero tarifa = Dinero.de(15.00);
        Dinero penalizacion = tarifa.porcentaje(0.50);
        assertEquals(7.50, penalizacion.getValor(), 0.001);
    }

    @Test
    @DisplayName("No debe permitir montos negativos")
    void testValidacionNegativos() {
        assertThrows(IllegalArgumentException.class, () -> Dinero.de(-5.00));
    }

    @Test
    @DisplayName("Debe respetar igualdad estructural por valor")
    void testIgualdadEstructural() {
        Dinero d1 = Dinero.de(39.00);
        Dinero d2 = Dinero.de(39.000);
        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }
}
