package com.irenaprokhyra.levelife.util;

import static org.junit.Assert.assertEquals;

import com.irenaprokhyra.levelife.model.Task;
import com.irenaprokhyra.levelife.model.TaskReward;

import org.junit.Test;

public class TaskRewardCalculatorTest {

    @Test
    public void calculateRewards_hardEcoTask_grantsAllRewards() {
        TaskReward reward = TaskRewardCalculator.calculateRewards(
                Task.DIFFICULTY_HARD,
                Task.CATEGORY_ECO
        );

        assertEquals(40, reward.getRewardXP());
        assertEquals(20, reward.getRewardBerries());
        assertEquals(3, reward.getEcoReward());
    }

    @Test
    public void calculateRewards_nonEcoTask_hasNoEcoCoins() {
        TaskReward reward = TaskRewardCalculator.calculateRewards(
                Task.DIFFICULTY_MEDIUM,
                Task.CATEGORY_GENERAL
        );

        assertEquals(20, reward.getRewardXP());
        assertEquals(10, reward.getRewardBerries());
        assertEquals(0, reward.getEcoReward());
    }

    @Test
    public void calculateRewards_emojiLabels_areNormalized() {
        TaskReward reward = TaskRewardCalculator.calculateRewards(
                "Dificil fire",
                "Sostenibilidad eco"
        );

        assertEquals(40, reward.getRewardXP());
        assertEquals(20, reward.getRewardBerries());
        assertEquals(3, reward.getEcoReward());
    }

    @Test
    public void calculateRewards_accentedDifficulty_isNormalized() {
        TaskReward reward = TaskRewardCalculator.calculateRewards(
                "Difícil",
                Task.CATEGORY_ECO
        );

        assertEquals(40, reward.getRewardXP());
        assertEquals(20, reward.getRewardBerries());
        assertEquals(3, reward.getEcoReward());
    }

    @Test
    public void calculateRewards_englishLabels_areNormalized() {
        TaskReward reward = TaskRewardCalculator.calculateRewards(
                "Hard",
                "Sustainability"
        );

        assertEquals(40, reward.getRewardXP());
        assertEquals(20, reward.getRewardBerries());
        assertEquals(3, reward.getEcoReward());
    }

    @Test
    public void calculateRewards_whitespaceAndEmojiNoise_areIgnored() {
        TaskReward reward = TaskRewardCalculator.calculateRewards(
                "   Difícil 🔥   ",
                "   Sostenibilidad ♻️   "
        );

        assertEquals(40, reward.getRewardXP());
        assertEquals(20, reward.getRewardBerries());
        assertEquals(3, reward.getEcoReward());
    }

    @Test
    public void calculateRewards_mediumEcoTask_grantsScaledEcoReward() {
        TaskReward reward = TaskRewardCalculator.calculateRewards(
                Task.DIFFICULTY_MEDIUM,
                Task.CATEGORY_ECO
        );

        assertEquals(20, reward.getRewardXP());
        assertEquals(10, reward.getRewardBerries());
        assertEquals(2, reward.getEcoReward());
    }

    @Test
    public void calculateRewards_hardNonEcoTask_hasHardRewardsWithoutEcoCoins() {
        TaskReward reward = TaskRewardCalculator.calculateRewards(
                Task.DIFFICULTY_HARD,
                Task.CATEGORY_GENERAL
        );

        assertEquals(40, reward.getRewardXP());
        assertEquals(20, reward.getRewardBerries());
        assertEquals(0, reward.getEcoReward());
    }

    @Test
    public void calculateRewards_sameInputs_areDeterministic() {
        TaskReward first = TaskRewardCalculator.calculateRewards(
                Task.DIFFICULTY_HARD,
                Task.CATEGORY_ECO
        );
        TaskReward second = TaskRewardCalculator.calculateRewards(
                Task.DIFFICULTY_HARD,
                Task.CATEGORY_ECO
        );

        assertEquals(first.getRewardXP(), second.getRewardXP());
        assertEquals(first.getRewardBerries(), second.getRewardBerries());
        assertEquals(first.getEcoReward(), second.getEcoReward());
    }

    @Test
    public void calculateRewards_generalCategoryNeverGrantsEcoCoins() {
        TaskReward reward = TaskRewardCalculator.calculateRewards(
                "Media",
                "General 🌍"
        );

        assertEquals(20, reward.getRewardXP());
        assertEquals(10, reward.getRewardBerries());
        assertEquals(0, reward.getEcoReward());
    }

    @Test
    public void calculateRewards_easyEcoTask_grantsScaledEcoReward() {
        TaskReward reward = TaskRewardCalculator.calculateRewards(
                Task.DIFFICULTY_EASY,
                Task.CATEGORY_ECO
        );

        assertEquals(10, reward.getRewardXP());
        assertEquals(5, reward.getRewardBerries());
        assertEquals(1, reward.getEcoReward());
    }

    @Test
    public void calculateRewards_mediumNonEcoTask_hasNoEcoCoins() {
        TaskReward reward = TaskRewardCalculator.calculateRewards(
                Task.DIFFICULTY_MEDIUM,
                Task.CATEGORY_GENERAL
        );

        assertEquals(20, reward.getRewardXP());
        assertEquals(10, reward.getRewardBerries());
        assertEquals(0, reward.getEcoReward());
    }
}