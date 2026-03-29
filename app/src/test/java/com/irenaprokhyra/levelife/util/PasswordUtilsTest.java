package com.irenaprokhyra.levelife.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordUtilsTest {

    @Test
    public void hashPassword_neverStoresRawValue() {
        String rawPassword = "1234";

        String hashedPassword = PasswordUtils.hashPassword(rawPassword);

        assertNotEquals(rawPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("pbkdf2_sha256$"));
    }

    @Test
    public void verifyPassword_acceptsCorrectPassword() {
        String rawPassword = "miPasswordSegura";
        String hashedPassword = PasswordUtils.hashPassword(rawPassword);

        assertTrue(PasswordUtils.verifyPassword(rawPassword, hashedPassword));
    }

    @Test
    public void verifyPassword_rejectsWrongPassword() {
        String hashedPassword = PasswordUtils.hashPassword("password-correcta");

        assertFalse(PasswordUtils.verifyPassword("otra-password", hashedPassword));
    }

    @Test
    public void verifyPassword_supportsLegacyPlaintextValues() {
        assertTrue(PasswordUtils.verifyPassword("1234", "1234"));
        assertTrue(PasswordUtils.needsUpgrade("1234"));
    }
}
