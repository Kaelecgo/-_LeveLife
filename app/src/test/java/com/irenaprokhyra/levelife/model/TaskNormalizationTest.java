package com.irenaprokhyra.levelife.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TaskNormalizationTest {

    @Test
    public void normalizeDifficulty_accentedValue_mapsToHard() {
        assertEquals(Task.DIFFICULTY_HARD, Task.normalizeDifficulty("Difícil"));
    }

    @Test
    public void normalizeDifficulty_englishValue_mapsCorrectly() {
        assertEquals(Task.DIFFICULTY_EASY, Task.normalizeDifficulty("Easy"));
        assertEquals(Task.DIFFICULTY_MEDIUM, Task.normalizeDifficulty("Medium"));
        assertEquals(Task.DIFFICULTY_HARD, Task.normalizeDifficulty("Hard"));
    }

    @Test
    public void normalizeDifficulty_withEmojiAndExtraWhitespace_mapsCorrectly() {
        assertEquals(Task.DIFFICULTY_EASY, Task.normalizeDifficulty("   Fácil ✅  "));
        assertEquals(Task.DIFFICULTY_HARD, Task.normalizeDifficulty("  Dificil 🔥 "));
    }

    @Test
    public void normalizeDifficulty_unknownValue_returnsSanitizedLabel() {
        assertEquals("Extrema", Task.normalizeDifficulty("Extrema!!!"));
    }

    @Test
    public void normalizeCategory_englishValue_mapsCorrectly() {
        assertEquals(Task.CATEGORY_HEALTH, Task.normalizeCategory("Health"));
        assertEquals(Task.CATEGORY_ECO, Task.normalizeCategory("Sustainability"));
        assertEquals(Task.CATEGORY_FOCUS, Task.normalizeCategory("Study"));
        assertEquals(Task.CATEGORY_SOCIAL, Task.normalizeCategory("Social"));
        assertEquals(Task.CATEGORY_GENERAL, Task.normalizeCategory("General"));
    }

    @Test
    public void normalizeCategory_withEmojiAndNoise_mapsCorrectly() {
        assertEquals(Task.CATEGORY_ECO, Task.normalizeCategory(" Sostenibilidad ♻️ "));
        assertEquals(Task.CATEGORY_HEALTH, Task.normalizeCategory("Salud y Físico 💪"));
    }

    @Test
    public void normalizeCategory_unknownValue_returnsSanitizedLabel() {
        assertEquals("Creatividad", Task.normalizeCategory("Creatividad!!!"));
    }

    @Test
    public void normalizeFrequency_englishValues_mapCorrectly() {
        assertEquals(Task.FREQUENCY_ONCE, Task.normalizeFrequency("Once"));
        assertEquals(Task.FREQUENCY_DAILY, Task.normalizeFrequency("Daily"));
        assertEquals(Task.FREQUENCY_WEEKLY, Task.normalizeFrequency("Weekly"));
        assertEquals(Task.FREQUENCY_MONTHLY, Task.normalizeFrequency("Monthly"));
    }

    @Test
    public void normalizeFrequency_legacyNormal_mapsToOnce() {
        assertEquals(Task.FREQUENCY_ONCE, Task.normalizeFrequency("Normal"));
    }

    @Test
    public void normalizeFrequency_blankValue_mapsToOnce() {
        assertEquals(Task.FREQUENCY_ONCE, Task.normalizeFrequency(""));
    }

    @Test
    public void normalizeFrequency_nullValue_mapsToOnce() {
        assertEquals(Task.FREQUENCY_ONCE, Task.normalizeFrequency(null));
    }

    @Test
    public void normalizeFrequency_withEmojiAndWhitespace_mapsCorrectly() {
        assertEquals(Task.FREQUENCY_DAILY, Task.normalizeFrequency("  Diaria 🔄 "));
        assertEquals(Task.FREQUENCY_WEEKLY, Task.normalizeFrequency(" Semanal 📅 "));
        assertEquals(Task.FREQUENCY_MONTHLY, Task.normalizeFrequency(" Mensual 🗓️ "));
    }

    @Test
    public void isKnownDifficulty_knownValues_returnTrue() {
        assertTrue(Task.isKnownDifficulty("Fácil"));
        assertTrue(Task.isKnownDifficulty("Media"));
        assertTrue(Task.isKnownDifficulty("Hard"));
    }

    @Test
    public void isKnownDifficulty_unknownValue_returnsFalse() {
        assertFalse(Task.isKnownDifficulty("Legendaria"));
    }

    @Test
    public void isKnownCategory_knownValues_returnTrue() {
        assertTrue(Task.isKnownCategory("Sostenibilidad"));
        assertTrue(Task.isKnownCategory("Health"));
        assertTrue(Task.isKnownCategory("General"));
    }

    @Test
    public void isKnownCategory_unknownValue_returnsFalse() {
        assertFalse(Task.isKnownCategory("Creatividad"));
    }

    @Test
    public void isKnownFrequency_knownValues_returnTrue() {
        assertTrue(Task.isKnownFrequency("Una vez"));
        assertTrue(Task.isKnownFrequency("Diaria"));
        assertTrue(Task.isKnownFrequency("Weekly"));
        assertTrue(Task.isKnownFrequency("Mensual"));
    }

    @Test
    public void isKnownFrequency_unknownValue_returnsFalse() {
        assertFalse(Task.isKnownFrequency("Anual"));
    }

    @Test
    public void constructor_simple_setsExpectedDefaults() {
        Task task = new Task(1, "Título", "Desc", "General", 10, 5);

        assertEquals(Task.CATEGORY_GENERAL, task.getCategory());
        assertEquals(Task.DIFFICULTY_MEDIUM, task.getDifficulty());
        assertEquals(Task.FREQUENCY_ONCE, task.getFrequency());
        assertEquals(0, task.getEcoReward());
        assertFalse(task.isEcoTask());
        assertFalse(task.isCompleted());
        assertEquals(0L, task.getLastCompletedAt());
    }

    @Test
    public void constructor_extended_normalizesDifficultyCategoryAndFrequency() {
        Task task = new Task(
                1,
                "Título",
                "Desc",
                "Sostenibilidad ♻️",
                40,
                20,
                3,
                "Difícil",
                "Diaria 🔄",
                true
        );

        assertEquals(Task.CATEGORY_ECO, task.getCategory());
        assertEquals(Task.DIFFICULTY_HARD, task.getDifficulty());
        assertEquals(Task.FREQUENCY_DAILY, task.getFrequency());
        assertEquals(3, task.getEcoReward());
        assertTrue(task.isEcoTask());
        assertFalse(task.isCompleted());
        assertEquals(0L, task.getLastCompletedAt());
    }

    @Test
    public void setters_normalizeIncomingValues() {
        Task task = new Task();

        task.setCategory("Health 💪");
        task.setDifficulty("Fácil ✅");
        task.setFrequency("Normal");

        assertEquals(Task.CATEGORY_HEALTH, task.getCategory());
        assertEquals(Task.DIFFICULTY_EASY, task.getDifficulty());
        assertEquals(Task.FREQUENCY_ONCE, task.getFrequency());
    }

    @Test
    public void isRecurring_onceFrequency_returnsFalse() {
        Task task = new Task();
        task.setFrequency("Una vez");

        assertFalse(task.isRecurring());
    }

    @Test
    public void isRecurring_legacyNormal_returnsFalse() {
        Task task = new Task();
        task.setFrequency("Normal");

        assertFalse(task.isRecurring());
    }

    @Test
    public void isRecurring_dailyWeeklyMonthly_returnTrue() {
        Task dailyTask = new Task();
        dailyTask.setFrequency("Diaria");

        Task weeklyTask = new Task();
        weeklyTask.setFrequency("Weekly");

        Task monthlyTask = new Task();
        monthlyTask.setFrequency("Mensual");

        assertTrue(dailyTask.isRecurring());
        assertTrue(weeklyTask.isRecurring());
        assertTrue(monthlyTask.isRecurring());
    }
}