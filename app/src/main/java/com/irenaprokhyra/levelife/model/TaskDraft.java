package com.irenaprokhyra.levelife.model;

public class TaskDraft {
    private final String title;
    private final String description;
    private final String category;
    private final String difficulty;
    private final String frequency;
    private final TaskReward reward;

    public TaskDraft(
            String title,
            String description,
            String category,
            String difficulty,
            String frequency,
            TaskReward reward
    ) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.frequency = frequency;
        this.reward = reward;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getDifficulty() { return difficulty; }
    public String getFrequency() { return frequency; }
    public TaskReward getReward() { return reward; }

    public boolean isEcoTask() {
        return reward.getEcoReward() > 0;
    }
}
