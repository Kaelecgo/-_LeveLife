package com.irenaprokhyra.levelife.util;

import com.irenaprokhyra.levelife.model.Task;
import com.irenaprokhyra.levelife.model.TaskReward;

public final class TaskRewardCalculator {

    private TaskRewardCalculator() {
    }

    public static TaskReward calculateRewards(String difficulty, String category) {
        String normalizedDifficulty = Task.normalizeDifficulty(difficulty);
        String normalizedCategory = Task.normalizeCategory(category);
        int rewardXP;
        int rewardBerries;
        int ecoReward = 0;

        if (Task.DIFFICULTY_HARD.equals(normalizedDifficulty)) {
            rewardXP = 40;
            rewardBerries = 20;
            ecoReward = 3;
        } else if (Task.DIFFICULTY_EASY.equals(normalizedDifficulty)) {
            rewardXP = 10;
            rewardBerries = 5;
            ecoReward = 1;
        } else {
            rewardXP = 20;
            rewardBerries = 10;
            ecoReward = 2;
        }

        if (!Task.CATEGORY_ECO.equals(normalizedCategory)) {
            ecoReward = 0;
        }

        return new TaskReward(rewardXP, rewardBerries, ecoReward);
    }
}
