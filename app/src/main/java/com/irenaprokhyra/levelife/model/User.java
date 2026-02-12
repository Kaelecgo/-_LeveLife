package com.irenaprokhyra.levelife.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "users")
public class User {
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
}
