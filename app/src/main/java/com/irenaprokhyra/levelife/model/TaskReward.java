package com.irenaprokhyra.levelife.model;

public class TaskReward {
    private final int rewardXP;
    private final int rewardBerries;
    private final int ecoReward;

    public TaskReward(int rewardXP, int rewardBerries, int ecoReward) {
        this.rewardXP = rewardXP;
        this.rewardBerries = rewardBerries;
        this.ecoReward = ecoReward;
    }

    public int getRewardXP() { return rewardXP; }
    public int getRewardBerries() { return rewardBerries; }
    public int getEcoReward() { return ecoReward; }
}
