package com.irenaprokhyra.levelife.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UserProgressTest {

    @Test
    public void constructor_initializesDefaultStateCorrectly() {
        User user = new User("Irena", "hashed-password");

        assertEquals("Irena", user.getName());
        assertEquals("hashed-password", user.getPasswordHash());
        assertEquals(1, user.getLevel());
        assertEquals(0, user.getExperience());
        assertEquals(0, user.getBerries());
        assertEquals(0, user.getEcoCoins());
    }

    @Test
    public void getXpToNextLevel_levelOne_returns100() {
        User user = new User("Irena", "hash");

        assertEquals(100, user.getXpToNextLevel());
    }

    @Test
    public void addExperience_lessThanThreshold_doesNotLevelUp() {
        User user = new User("Irena", "hash");

        boolean leveledUp = user.addExperience(50);

        assertFalse(leveledUp);
        assertEquals(1, user.getLevel());
        assertEquals(50, user.getExperience());
    }

    @Test
    public void addExperience_exactThreshold_levelsUpCorrectly() {
        User user = new User("Irena", "hash");

        boolean leveledUp = user.addExperience(100);

        assertTrue(leveledUp);
        assertEquals(2, user.getLevel());
        assertEquals(0, user.getExperience());
    }

    @Test
    public void addExperience_multipleLevelUps_preservesRemainder() {
        User user = new User("Irena", "hash");

        boolean leveledUp = user.addExperience(350);

        assertTrue(leveledUp);
        assertEquals(3, user.getLevel());
        assertEquals(50, user.getExperience());
    }

    @Test
    public void addExperience_zero_doesNotChangeState() {
        User user = new User("Irena", "hash");

        boolean leveledUp = user.addExperience(0);

        assertFalse(leveledUp);
        assertEquals(1, user.getLevel());
        assertEquals(0, user.getExperience());
    }

    @Test
    public void addExperience_negative_returnsFalseAndDoesNotCorruptState() {
        User user = new User("Irena", "hash");
        user.setLevel(2);
        user.setExperience(30);

        boolean leveledUp = user.addExperience(-10);

        assertFalse(leveledUp);
        assertEquals(2, user.getLevel());
        assertEquals(30, user.getExperience());
    }

    @Test
    public void addBerries_positive_increasesBalance() {
        User user = new User("Irena", "hash");

        user.addBerries(25);

        assertEquals(25, user.getBerries());
    }

    @Test
    public void addBerries_zero_doesNotChangeBalance() {
        User user = new User("Irena", "hash");
        user.setBerries(10);

        user.addBerries(0);

        assertEquals(10, user.getBerries());
    }

    @Test
    public void addBerries_negative_doesNotChangeBalance() {
        User user = new User("Irena", "hash");
        user.setBerries(10);

        user.addBerries(-5);

        assertEquals(10, user.getBerries());
    }

    @Test
    public void addEcoCoins_positive_increasesBalance() {
        User user = new User("Irena", "hash");

        user.addEcoCoins(3);

        assertEquals(3, user.getEcoCoins());
    }

    @Test
    public void addEcoCoins_zero_doesNotChangeBalance() {
        User user = new User("Irena", "hash");
        user.setEcoCoins(2);

        user.addEcoCoins(0);

        assertEquals(2, user.getEcoCoins());
    }

    @Test
    public void addEcoCoins_negative_doesNotChangeBalance() {
        User user = new User("Irena", "hash");
        user.setEcoCoins(2);

        user.addEcoCoins(-1);

        assertEquals(2, user.getEcoCoins());
    }

    @Test
    public void spendBerries_exactBalance_leavesZero() {
        User user = new User("Irena", "hash");
        user.setBerries(20);

        boolean success = user.spendBerries(20);

        assertTrue(success);
        assertEquals(0, user.getBerries());
    }

    @Test
    public void spendBerries_insufficientBalance_returnsFalseAndKeepsBalance() {
        User user = new User("Irena", "hash");
        user.setBerries(10);

        boolean success = user.spendBerries(20);

        assertFalse(success);
        assertEquals(10, user.getBerries());
    }

    @Test
    public void spendBerries_zero_returnsFalseAndKeepsBalance() {
        User user = new User("Irena", "hash");
        user.setBerries(10);

        boolean success = user.spendBerries(0);

        assertFalse(success);
        assertEquals(10, user.getBerries());
    }

    @Test
    public void spendBerries_negative_returnsFalseAndKeepsBalance() {
        User user = new User("Irena", "hash");
        user.setBerries(10);

        boolean success = user.spendBerries(-5);

        assertFalse(success);
        assertEquals(10, user.getBerries());
    }

    @Test
    public void getProgressPercentage_withPartialProgress_returnsExpectedValue() {
        User user = new User("Irena", "hash");
        user.setExperience(50);

        assertEquals(50, user.getProgressPercentage());
    }

    @Test
    public void getProgressPercentage_afterLevelUpRemainder_returnsExpectedValue() {
        User user = new User("Irena", "hash");
        user.setLevel(2);
        user.setExperience(50);

        assertEquals(25, user.getProgressPercentage());
    }

    @Test
    public void getProgressPercentage_zeroExperience_returnsZero() {
        User user = new User("Irena", "hash");

        assertEquals(0, user.getProgressPercentage());
    }
}
