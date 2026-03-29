package com.irenaprokhyra.levelife.util;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Locale;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtils {

    private static final String HASH_PREFIX = "pbkdf2_sha256";
    private static final int SALT_LENGTH_BYTES = 16;
    private static final int HASH_LENGTH_BITS = 256;
    private static final int ITERATIONS = 120000;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtils() {
    }

    public static String hashPassword(String rawPassword) {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(salt);

        byte[] hash = deriveKey(rawPassword, salt, ITERATIONS);
        return String.format(
                Locale.US,
                "%s$%d$%s$%s",
                HASH_PREFIX,
                ITERATIONS,
                toHex(salt),
                toHex(hash)
        );
    }

    public static boolean verifyPassword(String rawPassword, String storedValue) {
        if (rawPassword == null || storedValue == null || storedValue.isEmpty()) {
            return false;
        }

        if (!needsUpgrade(storedValue)) {
            try {
                String[] parts = storedValue.split("\\$");
                if (parts.length != 4) {
                    return false;
                }

                int iterations = Integer.parseInt(parts[1]);
                byte[] salt = fromHex(parts[2]);
                byte[] expectedHash = fromHex(parts[3]);
                byte[] actualHash = deriveKey(rawPassword, salt, iterations);
                return constantTimeEquals(expectedHash, actualHash);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        return storedValue.equals(rawPassword);
    }

    public static boolean needsUpgrade(String storedValue) {
        return storedValue == null || !storedValue.startsWith(HASH_PREFIX + "$");
    }

    private static byte[] deriveKey(String rawPassword, byte[] salt, int iterations) {
        try {
            PBEKeySpec keySpec = new PBEKeySpec(rawPassword.toCharArray(), salt, iterations, HASH_LENGTH_BITS);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return secretKeyFactory.generateSecret(keySpec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo proteger la contraseña", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            builder.append(String.format(Locale.US, "%02x", value));
        }
        return builder.toString();
    }

    private static byte[] fromHex(String value) {
        if (value.length() % 2 != 0) {
            throw new IllegalArgumentException("Cadena hexadecimal inválida");
        }

        byte[] result = new byte[value.length() / 2];
        for (int i = 0; i < value.length(); i += 2) {
            result[i / 2] = (byte) Integer.parseInt(value.substring(i, i + 2), 16);
        }
        return result;
    }

    private static boolean constantTimeEquals(byte[] first, byte[] second) {
        if (first.length != second.length) {
            return false;
        }

        int diff = 0;
        for (int i = 0; i < first.length; i++) {
            diff |= first[i] ^ second[i];
        }
        return diff == 0;
    }
}
