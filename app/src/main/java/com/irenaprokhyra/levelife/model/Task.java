package com.irenaprokhyra.levelife.model;

public class Task {
    private int id;
    private String title;
    private String description;
    private String category;
    private int rewardBerries;
    private int rewardXP;
    private boolean isCompleted;
    private String frequency;

    public Task() {}
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
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
}
