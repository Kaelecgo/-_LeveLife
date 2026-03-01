package com.irenaprokhyra.levelife.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Ignore; // Importante para métodos que no son columnas

@Entity(tableName = "users")
public class User {

    // >_ CONSTANTES DE EQUILIBRIO DEL JUEGO _<
    private static final int BASE_XP_PER_LEVEL = 100;

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_name")
    private String name;

    @ColumnInfo(name = "password")
    private String password;

    private int level;
    private int experience;
    private int berries;

    public User() {}

    // Añadimos @Ignore para que Room ignore este constructor
    // (Este lo usamos manualmente para crear usuarios nuevos)
    @Ignore
    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.level = 1;
        this.experience = 0;
        this.berries = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    public int getBerries() { return berries; }
    public void setBerries(int berries) { this.berries = berries; }

    // >_ LÓGICA DE NEGOCIO (GAMIFICACIÓN) _<
    /**
     * Calcula la XP necesaria para pasar al siguiente nivel.
     * Nivel Actual * 100 (Ej: Nivel 1 necesita 100, Nivel 2 necesita 200).
     */
    public int getXpToNextLevel() {
        return this.level * BASE_XP_PER_LEVEL;
    }

    /**
     * Añade XP y gestiona la subida de nivel.
     * @param amount Cantidad de XP ganada.
     * @return true si el usuario ha subido de nivel (para lanzar fuegos artificiales).
     */
    public boolean addExperience(int amount) {
        if (amount < 0) return false; // // Protección contra XP negativa

        this.experience += amount;
        int required = getXpToNextLevel();
        boolean leveledUp = false;

        // Bucle para subir múltiples niveles si la XP es muy grande
        while (this.experience >= required) {
            this.experience -= required; // Restamos lo usado
            this.level++;                // Subimos nivel
            required = getXpToNextLevel(); // Recalcula para el siguiente
            leveledUp = true;
        }
        return leveledUp;
    }
    /**
     * Añade bayas al monedero.
     */
    public void addBerries(int amount) {
        if (amount > 0) {
            this.berries += amount;
        }
    }

    /**
     * MEJORA -> PREPARACIÓN TIENDA
     * Intenta gastar monedas.
     * @param amount Cantidad a gastar.
     * @return true si la compra tuvo éxito, false si no hay saldo suficiente.
     */
    public boolean spendBerries(int amount) {
        if (amount > 0 && this.berries >= amount) {
            this.berries -= amount;
            return true; // Compra exitosa
        }
        return false;   // Fondos insuficientes
    }
    /**
     * MEJORA -> BARRA DE PROGRESO
     * Calcula el porcentaje de completado del nivel actual (0 a 100).
     * Útil para poner una ProgressBar en el futuro.
     */
    public int getProgressPercentage() {
        int required = getXpToNextLevel();
        if (required == 0) return 0; // Evitar división por cero
        return (this.experience * 100) / required;
    }
}
