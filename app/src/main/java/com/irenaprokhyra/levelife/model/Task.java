package com.irenaprokhyra.levelife.model;

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
    public static final String CATEGORY_HEALTH = "Salud y Fisico";
    public static final String CATEGORY_ECO = "Sostenibilidad";
    public static final String CATEGORY_FOCUS = "Estudio y trabajo";
    public static final String CATEGORY_SOCIAL = "Social y Ocio";
    public static final String CATEGORY_GENERAL = "General";

    public static final String DIFFICULTY_EASY = "Facil";
    public static final String DIFFICULTY_MEDIUM = "Media";
    public static final String DIFFICULTY_HARD = "Dificil";

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

    public static String normalizeCategory(String category) {
        String normalized = normalizeLabel(category);
        if (CATEGORY_HEALTH.equals(normalized)) return CATEGORY_HEALTH;
        if (CATEGORY_ECO.equals(normalized)) return CATEGORY_ECO;
        if (CATEGORY_FOCUS.equals(normalized)) return CATEGORY_FOCUS;
        if (CATEGORY_SOCIAL.equals(normalized)) return CATEGORY_SOCIAL;
        if (CATEGORY_GENERAL.equals(normalized)) return CATEGORY_GENERAL;
        return normalized;
    }

    public static String normalizeDifficulty(String difficulty) {
        String normalized = normalizeLabel(difficulty);
        if (DIFFICULTY_EASY.equals(normalized)) return DIFFICULTY_EASY;
        if (DIFFICULTY_MEDIUM.equals(normalized)) return DIFFICULTY_MEDIUM;
        if (DIFFICULTY_HARD.equals(normalized)) return DIFFICULTY_HARD;
        return normalized;
    }

    public static String normalizeFrequency(String frequency) {
        String normalized = normalizeLabel(frequency);
        if (FREQUENCY_DAILY.equals(normalized)) return FREQUENCY_DAILY;
        if (FREQUENCY_WEEKLY.equals(normalized)) return FREQUENCY_WEEKLY;
        if (FREQUENCY_MONTHLY.equals(normalized)) return FREQUENCY_MONTHLY;
        if (FREQUENCY_ONCE.equals(normalized) || normalized.isEmpty()) return FREQUENCY_ONCE;
        return normalized;
    }

    private static String normalizeLabel(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replaceAll("[^\\p{L}\\p{Nd} ]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
