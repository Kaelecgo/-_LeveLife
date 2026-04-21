package com.irenaprokhyra.levelife.model;

import java.text.Normalizer;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "tasks",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "user_id")}
)
public class Task {
    public static final String CATEGORY_HEALTH = "Salud y Físico";
    public static final String CATEGORY_ECO = "Sostenibilidad";
    public static final String CATEGORY_FOCUS = "Estudio y trabajo";
    public static final String CATEGORY_SOCIAL = "Social y Ocio";
    public static final String CATEGORY_GENERAL = "General";

    public static final String DIFFICULTY_EASY = "Fácil";
    public static final String DIFFICULTY_MEDIUM = "Media";
    public static final String DIFFICULTY_HARD = "Difícil";

    public static final String FREQUENCY_ONCE = "Una vez";
    public static final String FREQUENCY_DAILY = "Diaria";
    public static final String FREQUENCY_WEEKLY = "Semanal";
    public static final String FREQUENCY_MONTHLY = "Mensual";

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "reward_berries")
    private int rewardBerries;

    @ColumnInfo(name = "reward_xp")
    private int rewardXP;

    @ColumnInfo(name = "eco_reward", defaultValue = "0")
    private int ecoReward;

    @ColumnInfo(name = "is_eco_task", defaultValue = "0")
    private boolean isEcoTask;

    @ColumnInfo(name = "last_completed_at", defaultValue = "0")
    private long lastCompletedAt;

    private String title;
    private String description;
    private String category;
    private String difficulty;
    private boolean isCompleted;
    private String frequency;

    public Task() {
    }

    @Ignore
    public Task(int userId, String title, String description, String category, int rewardXP, int rewardBerries) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.category = normalizeCategory(category);
        this.rewardXP = rewardXP;
        this.rewardBerries = rewardBerries;
        this.ecoReward = 0;
        this.isEcoTask = false;
        this.difficulty = DIFFICULTY_MEDIUM;
        this.isCompleted = false;
        this.frequency = FREQUENCY_ONCE;
        this.lastCompletedAt = 0L;
    }

    @Ignore
    public Task(
            int userId,
            String title,
            String description,
            String category,
            int rewardXP,
            int rewardBerries,
            int ecoReward,
            String difficulty,
            String frequency,
            boolean isEcoTask
    ) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.category = normalizeCategory(category);
        this.rewardXP = rewardXP;
        this.rewardBerries = rewardBerries;
        this.ecoReward = ecoReward;
        this.difficulty = normalizeDifficulty(difficulty);
        this.frequency = normalizeFrequency(frequency);
        this.isEcoTask = isEcoTask;
        this.isCompleted = false;
        this.lastCompletedAt = 0L;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRewardBerries() { return rewardBerries; }
    public void setRewardBerries(int rewardBerries) { this.rewardBerries = rewardBerries; }

    public int getRewardXP() { return rewardXP; }
    public void setRewardXP(int rewardXP) { this.rewardXP = rewardXP; }

    public int getEcoReward() { return ecoReward; }
    public void setEcoReward(int ecoReward) { this.ecoReward = ecoReward; }

    public boolean isEcoTask() { return isEcoTask; }
    public void setEcoTask(boolean ecoTask) { isEcoTask = ecoTask; }

    public long getLastCompletedAt() { return lastCompletedAt; }
    public void setLastCompletedAt(long lastCompletedAt) { this.lastCompletedAt = lastCompletedAt; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = normalizeCategory(category); }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = normalizeDifficulty(difficulty); }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = normalizeFrequency(frequency); }

    public boolean isRecurring() {
        return !FREQUENCY_ONCE.equals(normalizeFrequency(frequency));
    }

    public static boolean isKnownCategory(String category) {
        String normalized = normalizeCategory(category);
        return CATEGORY_HEALTH.equals(normalized)
                || CATEGORY_ECO.equals(normalized)
                || CATEGORY_FOCUS.equals(normalized)
                || CATEGORY_SOCIAL.equals(normalized)
                || CATEGORY_GENERAL.equals(normalized);
    }

    public static boolean isKnownDifficulty(String difficulty) {
        String normalized = normalizeDifficulty(difficulty);
        return DIFFICULTY_EASY.equals(normalized)
                || DIFFICULTY_MEDIUM.equals(normalized)
                || DIFFICULTY_HARD.equals(normalized);
    }

    public static boolean isKnownFrequency(String frequency) {
        String normalized = normalizeFrequency(frequency);
        return FREQUENCY_ONCE.equals(normalized)
                || FREQUENCY_DAILY.equals(normalized)
                || FREQUENCY_WEEKLY.equals(normalized)
                || FREQUENCY_MONTHLY.equals(normalized);
    }

    public static String normalizeCategory(String category) {
        String normalized = normalizeLabel(category);
        if (startsWithLabel(normalized, CATEGORY_HEALTH) || startsWithLabel(normalized, "Health")) return CATEGORY_HEALTH;
        if (startsWithLabel(normalized, CATEGORY_ECO) || startsWithLabel(normalized, "Sustainability")) return CATEGORY_ECO;
        if (startsWithLabel(normalized, CATEGORY_FOCUS) || startsWithLabel(normalized, "Study")) return CATEGORY_FOCUS;
        if (startsWithLabel(normalized, CATEGORY_SOCIAL) || startsWithLabel(normalized, "Social")) return CATEGORY_SOCIAL;
        if (startsWithLabel(normalized, CATEGORY_GENERAL) || startsWithLabel(normalized, "General")) return CATEGORY_GENERAL;
        return normalized;
    }

    public static String normalizeDifficulty(String difficulty) {
        String normalized = normalizeLabel(difficulty);
        if (startsWithLabel(normalized, DIFFICULTY_EASY) || startsWithLabel(normalized, "Easy")) return DIFFICULTY_EASY;
        if (startsWithLabel(normalized, DIFFICULTY_MEDIUM) || startsWithLabel(normalized, "Medium")) return DIFFICULTY_MEDIUM;
        if (startsWithLabel(normalized, DIFFICULTY_HARD) || startsWithLabel(normalized, "Hard")) return DIFFICULTY_HARD;
        return normalized;
    }

    public static String normalizeFrequency(String frequency) {
        String normalized = normalizeLabel(frequency);

        if (startsWithLabel(normalized, FREQUENCY_DAILY) || startsWithLabel(normalized, "Daily")) {
            return FREQUENCY_DAILY;
        }
        if (startsWithLabel(normalized, FREQUENCY_WEEKLY) || startsWithLabel(normalized, "Weekly")) {
            return FREQUENCY_WEEKLY;
        }
        if (startsWithLabel(normalized, FREQUENCY_MONTHLY) || startsWithLabel(normalized, "Monthly")) {
            return FREQUENCY_MONTHLY;
        }

        if (startsWithLabel(normalized, "Normal")) {
            return FREQUENCY_ONCE;
        }

        if (startsWithLabel(normalized, FREQUENCY_ONCE)
                || startsWithLabel(normalized, "Once")
                || normalized.isEmpty()) {
            return FREQUENCY_ONCE;
        }

        return normalized;
    }

    private static String normalizeLabel(String value) {
        if (value == null) {
            return "";
        }

        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replaceAll("[^\\p{L}\\p{Nd} ]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static boolean startsWithLabel(String value, String prefix) {
        String normalizedValue = normalizeLabel(value);
        String normalizedPrefix = normalizeLabel(prefix);
        return normalizedValue.regionMatches(true, 0, normalizedPrefix, 0, normalizedPrefix.length());
    }
}
