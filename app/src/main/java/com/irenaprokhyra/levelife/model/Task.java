package com.irenaprokhyra.levelife.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// >_ CLAVES FORÁNEAS E ÍNDICES _<
// Vinculamos la tarea al usuario. Si el usuario se borra (CASCADE), sus tareas también.
// Creamos un índice en user_id para que las búsquedas sean ultra rápidas.
@Entity(tableName = "tasks",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id", childColumns = "user_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "user_id")})
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "reward_berries")
    private int rewardBerries;

    @ColumnInfo(name = "reward_xp")
    private int rewardXP;

    private String title;
    private String description;
    private String category;
    private boolean isCompleted;
    private String frequency;

    public static final String CATEGORY_HEALTH = "Salud y Fisico";
    public static final String CATEGORY_ECO = "Sostenibilidad";
    public static final String CATEGORY_FOCUS = "Estudio y trabajo";
    public static final String CATEGORY_SOCIAL = "Social y Ocio";
    public static final String CATEGORY_GENERAL = "General";


    // Constructor vacio obligatorio para Room
    public Task() {}

    // >_ MEJORA 3: CONSTRUCTOR DE CONVENIENCIA _<
    // Para crear tareas fácilmente en el código (ej: en el Seeder)
    @Ignore
    public Task(int userId, String title, String description, String category, int rewardXP, int rewardBerries) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.rewardXP = rewardXP;
        this.rewardBerries = rewardBerries;
        this.isCompleted = false; // Por defecto no esta hecha
        this.frequency = "Normal"; // Valor por defecto
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getRewardBerries() { return rewardBerries; }
    public void setRewardBerries(int rewardBerries) { this.rewardBerries = rewardBerries; }

    public int getRewardXP() { return rewardXP; }
    public void setRewardXP(int rewardXP) { this.rewardXP = rewardXP; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    // >_ METODO DE CAMBIO DE ESTADO _<
    public void toggleCompleted() {
        this.isCompleted = !this.isCompleted;
    }
}