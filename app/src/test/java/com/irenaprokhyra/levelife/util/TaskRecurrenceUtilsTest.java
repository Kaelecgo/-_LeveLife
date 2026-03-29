package com.irenaprokhyra.levelife.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.irenaprokhyra.levelife.model.Task;

import org.junit.Test;

import java.util.Calendar;

public class TaskRecurrenceUtilsTest {

    @Test
    public void wasCompletedInCurrentPeriod_dailyTask_sameDay_returnsTrue() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 0, 0);

        Calendar earlierToday = (Calendar) now.clone();
        earlierToday.set(Calendar.HOUR_OF_DAY, 9);

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_DAILY,
                earlierToday.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_weeklyTask_previousWeek_returnsFalse() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 0, 0);

        Calendar lastWeek = (Calendar) now.clone();
        lastWeek.add(Calendar.WEEK_OF_YEAR, -1);

        assertFalse(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_WEEKLY,
                lastWeek.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_emojiFrequency_sameDayReturnsTrue() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 0, 0);

        Calendar earlierToday = (Calendar) now.clone();
        earlierToday.set(Calendar.HOUR_OF_DAY, 9);

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                "Diaria 🔄",
                earlierToday.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }
}
