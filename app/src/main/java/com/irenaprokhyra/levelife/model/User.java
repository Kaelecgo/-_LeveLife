package com.irenaprokhyra.levelife.model;

public class User {
    private int id;
    private String name;
    private int level;
    private int experience;
    private int berries;

    public User() {}
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }
    public int getBerries() { return berries; }
    public void setBerries(int berries) { this.berries = berries; }
}
