package com.irenaprokhyra.levelife.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class UserTest {

    private User testUser;

    // @Before se ejecuta SIEMPRE antes de cada test
    // Nos asegura que cada prueba empieza con un usuario limpio, Nivel 1 y 0 XP
    @Before
    public void setUp() {
        testUser = new User("testPlayer", "1234");
    }

    @Test
    public void addExperience_normalGain_updatesExperienceOnly() {
        // Actuar: Damos 50 XP (El nivel 1 pide 100 XP)
        boolean leveledUp = testUser.addExperience(50);

        // Comprobar: No debe subir de nivel - solo sumar XP
        assertFalse(leveledUp);
        assertEquals(1, testUser.getLevel());
        assertEquals(50, testUser.getExperience());
    }

    @Test
    public void addExperience_levelUp_calculatesRemainderCorrectly() {
        // Actuar: Damos 150 XP
        // El lvl 1 pide 100 XP. Debe sobrar 50 XP para el lvl 2
        boolean leveledUp = testUser.addExperience(150);

        // Comprobar: Debe subir de nivel y retener 50 XP
        assertTrue(leveledUp);
        assertEquals(2, testUser.getLevel());
        assertEquals(50, testUser.getExperience());
    }

    @Test
    public void addExperience_negativeValue_isIgnores() {
        // Actuar: Damos un valor negativo
        testUser.addExperience(50); // Ponemos 50 positivos
        testUser.addExperience(-100); // Intentamos robarle XP

        // Comprobar: El metodo debe haber bloqueado el -100
        assertEquals(50, testUser.getExperience());
    }

    // >_ TESTS DE ECONOMÍA (TIENDA) _<
    @Test
    public void spendBerries_sufficientFunds_rdeductsBerriesReturnsTrue() {
        // Preparar: le damos 100 bayas
        testUser.addBerries(100);

        // Actuar: Compramos un mueble de 40 bayas
        boolean purchaseSuccessful = testUser.spendBerries(40);

        // Comprobar: La compra aceptada y quedan 60 bayas
        assertTrue(purchaseSuccessful);
        assertEquals(60, testUser.getBerries());
    }

    @Test
    public void spendBerries_insufficientFunds_blocksPurchaseAndReturnsFalse() {
        // Preparar: le damos 10 bayas
        testUser.addBerries(10);

        // Actuar: Intentamos comprar algo de 50 bayas
        boolean puchaseSuccessful = testUser.spendBerries(50);

        // Comprobar: La compra denegada y las bayas no se tocan
        assertFalse(puchaseSuccessful);
        assertEquals(10, testUser.getBerries());
    }
}
