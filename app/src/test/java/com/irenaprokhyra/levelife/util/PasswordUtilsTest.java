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
    public void hashPassword_sameRawPasswordTwice_generatesDifferentHashes() {
        String rawPassword = "miPasswordSegura123";

        String firstHash = PasswordUtils.hashPassword(rawPassword);
        String secondHash = PasswordUtils.hashPassword(rawPassword);

        assertNotEquals(firstHash, secondHash);
        assertTrue(firstHash.startsWith("pbkdf2_sha256$"));
        assertTrue(secondHash.startsWith("pbkdf2_sha256$"));
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
    public void verifyPassword_rejectsCaseChangedPassword() {
        String hashedPassword = PasswordUtils.hashPassword("MiPasswordSegura");

        assertFalse(PasswordUtils.verifyPassword("mipasswordsegura", hashedPassword));
    }

    @Test
    public void verifyPassword_nullStoredValue_returnsFalse() {
        assertFalse(PasswordUtils.verifyPassword("1234", null));
    }

    @Test
    public void verifyPassword_nullRawPassword_returnsFalse() {
        String hashedPassword = PasswordUtils.hashPassword("1234");

        assertFalse(PasswordUtils.verifyPassword(null, hashedPassword));
    }

    @Test
    public void verifyPassword_blankStoredValue_returnsFalse() {
        assertFalse(PasswordUtils.verifyPassword("1234", ""));
    }

    @Test
    public void verifyPassword_malformedHashMissingParts_returnsFalse() {
        String malformed = "pbkdf2_sha256$120000$abcd";

        assertFalse(PasswordUtils.verifyPassword("1234", malformed));
    }

    @Test
    public void verifyPassword_malformedHashInvalidIterations_returnsFalse() {
        String malformed = "pbkdf2_sha256$abc$abcd$ef12";

        assertFalse(PasswordUtils.verifyPassword("1234", malformed));
    }

    @Test
    public void verifyPassword_malformedHashInvalidHex_returnsFalse() {
        String malformed = "pbkdf2_sha256$120000$zzzz$abcd";

        assertFalse(PasswordUtils.verifyPassword("1234", malformed));
    }

    @Test
    public void verifyPassword_supportsLegacyPlaintextValues() {
        assertTrue(PasswordUtils.verifyPassword("1234", "1234"));
        assertTrue(PasswordUtils.needsUpgrade("1234"));
    }

    @Test
    public void needsUpgrade_hashedPassword_returnsFalse() {
        String hashedPassword = PasswordUtils.hashPassword("miPasswordSegura");

        assertFalse(PasswordUtils.needsUpgrade(hashedPassword));
    }

    @Test
    public void needsUpgrade_nullValue_returnsTrue() {
        assertTrue(PasswordUtils.needsUpgrade(null));
    }

    @Test
    public void needsUpgrade_legacyPlaintext_returnsTrue() {
        assertTrue(PasswordUtils.needsUpgrade("1234"));
    }
}