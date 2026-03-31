package com.irenaprokhyra.levelife.util;

import static org.junit.Assert.assertEquals;

import com.irenaprokhyra.levelife.model.Task;
import com.irenaprokhyra.levelife.model.TaskReward;

import org.junit.Test;

public class TaskRewardCalculatorTest {

    @Test
    public void calculateRewards_hardEcoTask_grantsAllRewards() {
        TaskReward reward = TaskRewardCalculator.calculateRewards(Task.DIFFICULTY_HARD, Task.CATEGORY_ECO);

        assertEquals(40, reward.getRewardXP());
        assertEquals(20, reward.getRewardBerries());
        assertEquals(3, reward.getEcoReward());
    }

    @Test
    public void calculateRewards_nonEcoTask_hasNoEcoCoins() {
        TaskReward reward = TaskRewardCalculator.calculateRewards(Task.DIFFICULTY_MEDIUM, Task.CATEGORY_GENERAL);

        assertEquals(20, reward.getRewardXP());
        assertEquals(10, reward.getRewardBerries());
        assertEquals(0, reward.getEcoReward());
    }

    @Test
    public void calculateRewards_emojiLabels_areNormalized() {
        TaskReward reward = TaskRewardCalculator.calculateRewards("Dificil fire", "Sostenibilidad eco");

        assertEquals(40, reward.getRewardXP());
        assertEquals(20, reward.getRewardBerries());
        assertEquals(3, reward.getEcoReward());
    }

    @Test
    public void calculateRewards_accentedDifficulty_isNormalized() {
        TaskReward reward = TaskRewardCalculator.calculateRewards("Dif\u00edcil", Task.CATEGORY_ECO);

        assertEquals(40, reward.getRewardXP());
        assertEquals(20, reward.getRewardBerries());
        assertEquals(3, reward.getEcoReward());
    }
}
