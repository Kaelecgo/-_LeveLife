package com.irenaprokhyra.levelife.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.Index;

@Entity(
        tableName = "users",
        indices = {@Index(value = "user_name", unique = true)}
)
public class User {

    private static final int BASE_XP_PER_LEVEL = 100;
    public static final int FREQUENCY_HINT_DAILY = 1;
    public static final int FREQUENCY_HINT_WEEKLY = 1 << 1;
    public static final int FREQUENCY_HINT_MONTHLY = 1 << 2;
    public static final int FREQUENCY_HINT_ONCE = 1 << 3;
    public static final int CURRENT_STARTER_TASK_PACK_VERSION = 3;

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_name")
    private String name;

    @ColumnInfo(name = "password")
    private String passwordHash;

    private int level;
    private int experience;
    private int berries;

    @ColumnInfo(name = "eco_coins", defaultValue = "0")
    private int ecoCoins;

    @ColumnInfo(name = "seen_frequency_hints_mask", defaultValue = "0")
    private int seenFrequencyHintsMask;

    @ColumnInfo(name = "starter_task_pack_version", defaultValue = "0")
    private int starterTaskPackVersion;

    public User() {}

    @Ignore
    public User(String name, String passwordHash) {
        this.name = name;
        this.passwordHash = passwordHash;
        this.level = 1;
        this.experience = 0;
        this.berries = 0;
        this.ecoCoins = 0;
        this.seenFrequencyHintsMask = 0;
        this.starterTaskPackVersion = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    public int getBerries() { return berries; }
    public void setBerries(int berries) { this.berries = berries; }

    public int getEcoCoins() { return ecoCoins; }
    public void setEcoCoins(int ecoCoins) { this.ecoCoins = ecoCoins; }

    public int getSeenFrequencyHintsMask() { return seenFrequencyHintsMask; }
    public void setSeenFrequencyHintsMask(int seenFrequencyHintsMask) {
        this.seenFrequencyHintsMask = seenFrequencyHintsMask;
    }

    public int getStarterTaskPackVersion() { return starterTaskPackVersion; }
    public void setStarterTaskPackVersion(int starterTaskPackVersion) {
        this.starterTaskPackVersion = starterTaskPackVersion;
    }

    public int getXpToNextLevel() {
        return this.level * BASE_XP_PER_LEVEL;
    }

    public boolean addExperience(int amount) {
        if (amount < 0) return false;
        this.experience += amount;
        int required = getXpToNextLevel();
        boolean leveledUp = false;
        while (this.experience >= required) {
            this.experience -= required;
            this.level++;
            required = getXpToNextLevel();
            leveledUp = true;
        }
        return leveledUp;
    }

    public void addBerries(int amount) {
        if (amount > 0) {
            this.berries += amount;
        }
    }

    public void addEcoCoins(int amount) {
        if (amount > 0) {
            this.ecoCoins += amount;
        }
    }

    public boolean spendBerries(int amount) {
        if (amount > 0 && this.berries >= amount) {
            this.berries -= amount;
            return true;
        }
        return false;
    }

    public boolean spendEcoCoins(int amount) {
        if (amount > 0 && this.ecoCoins >= amount) {
            this.ecoCoins -= amount;
            return true;
        }
        return false;
    }

    public int getProgressPercentage() {
        int required = getXpToNextLevel();
        if (required == 0) return 0;
        return (this.experience * 100) / required;
    }
}
