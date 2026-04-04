package com.irenaprokhyra.levelife.util;

import static org.junit.Assert.assertEquals;
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
        now.set(Calendar.MILLISECOND, 0);

        Calendar earlierToday = (Calendar) now.clone();
        earlierToday.set(Calendar.HOUR_OF_DAY, 9);

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_DAILY,
                earlierToday.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_dailyTask_previousDay_returnsFalse() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 0, 0);
        now.set(Calendar.MILLISECOND, 0);

        Calendar yesterday = (Calendar) now.clone();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        assertFalse(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_DAILY,
                yesterday.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_dailyTask_exactlyAtDayStart_returnsTrue() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 0, 0);
        now.set(Calendar.MILLISECOND, 0);

        Calendar startOfDay = (Calendar) now.clone();
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_DAILY,
                startOfDay.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_dailyTask_oneMillisecondBeforeDayStart_returnsFalse() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 0, 0);
        now.set(Calendar.MILLISECOND, 0);

        Calendar startOfDay = (Calendar) now.clone();
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);

        assertFalse(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_DAILY,
                startOfDay.getTimeInMillis() - 1L,
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_weeklyTask_sameWeek_returnsTrue() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 0, 0);
        now.set(Calendar.MILLISECOND, 0);

        Calendar earlierThisWeek = (Calendar) now.clone();
        earlierThisWeek.set(2026, Calendar.MARCH, 27, 10, 0, 0);

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_WEEKLY,
                earlierThisWeek.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_weeklyTask_previousWeek_returnsFalse() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 0, 0);
        now.set(Calendar.MILLISECOND, 0);

        Calendar lastWeek = (Calendar) now.clone();
        lastWeek.add(Calendar.DAY_OF_YEAR, -7);

        assertFalse(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_WEEKLY,
                lastWeek.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_weeklyTask_exactlyAtWeekStart_returnsTrue() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 0, 0);
        now.set(Calendar.MILLISECOND, 0);

        Calendar startOfWeek = Calendar.getInstance();
        startOfWeek.setFirstDayOfWeek(Calendar.MONDAY);
        startOfWeek.set(2026, Calendar.MARCH, 23, 0, 0, 0);
        startOfWeek.set(Calendar.MILLISECOND, 0);

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_WEEKLY,
                startOfWeek.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_weeklyTask_oneMillisecondBeforeWeekStart_returnsFalse() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 0, 0);
        now.set(Calendar.MILLISECOND, 0);

        Calendar startOfWeek = Calendar.getInstance();
        startOfWeek.setFirstDayOfWeek(Calendar.MONDAY);
        startOfWeek.set(2026, Calendar.MARCH, 23, 0, 0, 0);
        startOfWeek.set(Calendar.MILLISECOND, 0);

        assertFalse(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_WEEKLY,
                startOfWeek.getTimeInMillis() - 1L,
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_monthlyTask_sameMonth_returnsTrue() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 0, 0);
        now.set(Calendar.MILLISECOND, 0);

        Calendar earlierThisMonth = (Calendar) now.clone();
        earlierThisMonth.set(Calendar.DAY_OF_MONTH, 5);

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_MONTHLY,
                earlierThisMonth.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_monthlyTask_previousMonth_returnsFalse() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 0, 0);
        now.set(Calendar.MILLISECOND, 0);

        Calendar previousMonth = (Calendar) now.clone();
        previousMonth.add(Calendar.MONTH, -1);

        assertFalse(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_MONTHLY,
                previousMonth.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_monthlyTask_exactlyAtMonthStart_returnsTrue() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 0, 0);
        now.set(Calendar.MILLISECOND, 0);

        Calendar startOfMonth = (Calendar) now.clone();
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        startOfMonth.set(Calendar.MINUTE, 0);
        startOfMonth.set(Calendar.SECOND, 0);
        startOfMonth.set(Calendar.MILLISECOND, 0);

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_MONTHLY,
                startOfMonth.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_monthlyTask_oneMillisecondBeforeMonthStart_returnsFalse() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 0, 0);
        now.set(Calendar.MILLISECOND, 0);

        Calendar startOfMonth = (Calendar) now.clone();
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        startOfMonth.set(Calendar.MINUTE, 0);
        startOfMonth.set(Calendar.SECOND, 0);
        startOfMonth.set(Calendar.MILLISECOND, 0);

        assertFalse(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_MONTHLY,
                startOfMonth.getTimeInMillis() - 1L,
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_emojiFrequency_sameDay_returnsTrue() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 0, 0);
        now.set(Calendar.MILLISECOND, 0);

        Calendar earlierToday = (Calendar) now.clone();
        earlierToday.set(Calendar.HOUR_OF_DAY, 9);

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                "Diaria 🔄",
                earlierToday.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_englishFrequency_sameDay_returnsTrue() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 0, 0);
        now.set(Calendar.MILLISECOND, 0);

        Calendar earlierToday = (Calendar) now.clone();
        earlierToday.set(Calendar.HOUR_OF_DAY, 9);

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                "Daily",
                earlierToday.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void getCurrentPeriodStart_daily_returnsStartOfSameDay() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 45, 30);
        now.set(Calendar.MILLISECOND, 123);

        long periodStart = TaskRecurrenceUtils.getCurrentPeriodStart(
                Task.FREQUENCY_DAILY,
                now.getTimeInMillis()
        );

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(periodStart);

        assertEquals(2026, start.get(Calendar.YEAR));
        assertEquals(Calendar.MARCH, start.get(Calendar.MONTH));
        assertEquals(29, start.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, start.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, start.get(Calendar.MINUTE));
        assertEquals(0, start.get(Calendar.SECOND));
        assertEquals(0, start.get(Calendar.MILLISECOND));
    }

    @Test
    public void getCurrentPeriodStart_weekly_returnsStartOfCurrentWeek() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 45, 30);
        now.set(Calendar.MILLISECOND, 123);

        long periodStart = TaskRecurrenceUtils.getCurrentPeriodStart(
                Task.FREQUENCY_WEEKLY,
                now.getTimeInMillis()
        );

        Calendar expected = Calendar.getInstance();
        expected.setFirstDayOfWeek(Calendar.MONDAY);
        expected.set(2026, Calendar.MARCH, 23, 0, 0, 0);
        expected.set(Calendar.MILLISECOND, 0);

        assertEquals(expected.getTimeInMillis(), periodStart);
    }

    @Test
    public void getCurrentPeriodStart_monthly_returnsFirstDayOfMonthAtMidnight() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 45, 30);
        now.set(Calendar.MILLISECOND, 123);

        long periodStart = TaskRecurrenceUtils.getCurrentPeriodStart(
                Task.FREQUENCY_MONTHLY,
                now.getTimeInMillis()
        );

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(periodStart);

        assertEquals(2026, start.get(Calendar.YEAR));
        assertEquals(Calendar.MARCH, start.get(Calendar.MONTH));
        assertEquals(1, start.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, start.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, start.get(Calendar.MINUTE));
        assertEquals(0, start.get(Calendar.SECOND));
        assertEquals(0, start.get(Calendar.MILLISECOND));
    }

    @Test
    public void getCurrentPeriodStart_once_returnsZero() {
        Calendar now = Calendar.getInstance();
        now.set(2026, Calendar.MARCH, 29, 15, 45, 30);
        now.set(Calendar.MILLISECOND, 123);

        long periodStart = TaskRecurrenceUtils.getCurrentPeriodStart(
                Task.FREQUENCY_ONCE,
                now.getTimeInMillis()
        );

        assertEquals(0L, periodStart);
    }
}
