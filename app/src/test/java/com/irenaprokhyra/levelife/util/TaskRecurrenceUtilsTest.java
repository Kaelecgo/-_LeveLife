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
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 0, 0, 0);
        Calendar earlierToday = calendarOf(2026, Calendar.MARCH, 29, 9, 0, 0, 0);

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_DAILY,
                earlierToday.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_dailyTask_previousDay_returnsFalse() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 0, 0, 0);
        Calendar yesterday = calendarOf(2026, Calendar.MARCH, 28, 15, 0, 0, 0);

        assertFalse(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_DAILY,
                yesterday.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_dailyTask_exactlyAtDayStart_returnsTrue() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 0, 0, 0);
        Calendar startOfDay = calendarOf(2026, Calendar.MARCH, 29, 0, 0, 0, 0);

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_DAILY,
                startOfDay.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_dailyTask_oneMillisecondBeforeDayStart_returnsFalse() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 0, 0, 0);
        Calendar startOfDay = calendarOf(2026, Calendar.MARCH, 29, 0, 0, 0, 0);

        assertFalse(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_DAILY,
                startOfDay.getTimeInMillis() - 1L,
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_weeklyTask_sameWeek_returnsTrue() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 0, 0, 0); // Domingo
        Calendar earlierThisWeek = calendarOf(2026, Calendar.MARCH, 27, 10, 0, 0, 0); // Viernes

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_WEEKLY,
                earlierThisWeek.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_weeklyTask_previousWeek_returnsFalse() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 0, 0, 0); // Domingo
        Calendar lastWeek = calendarOf(2026, Calendar.MARCH, 22, 15, 0, 0, 0); // Domingo anterior

        assertFalse(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_WEEKLY,
                lastWeek.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_weeklyTask_exactlyAtWeekStart_returnsTrue() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 0, 0, 0); // Domingo
        Calendar startOfWeek = calendarOf(2026, Calendar.MARCH, 23, 0, 0, 0, 0); // Lunes

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_WEEKLY,
                startOfWeek.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_weeklyTask_oneMillisecondBeforeWeekStart_returnsFalse() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 0, 0, 0); // Domingo
        Calendar startOfWeek = calendarOf(2026, Calendar.MARCH, 23, 0, 0, 0, 0); // Lunes

        assertFalse(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_WEEKLY,
                startOfWeek.getTimeInMillis() - 1L,
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_weeklyTask_englishFrequency_sameWeek_returnsTrue() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 0, 0, 0); // Domingo
        Calendar earlierThisWeek = calendarOf(2026, Calendar.MARCH, 27, 10, 0, 0, 0); // Viernes

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                "Weekly",
                earlierThisWeek.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_weeklyTask_whenNowIsMonday_previousSunday_returnsFalse() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 30, 10, 15, 0, 0); // Lunes
        Calendar previousSunday = calendarOf(2026, Calendar.MARCH, 29, 23, 59, 59, 999); // Domingo anterior

        assertFalse(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_WEEKLY,
                previousSunday.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_monthlyTask_sameMonth_returnsTrue() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 0, 0, 0);
        Calendar earlierThisMonth = calendarOf(2026, Calendar.MARCH, 5, 15, 0, 0, 0);

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_MONTHLY,
                earlierThisMonth.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_monthlyTask_previousMonth_returnsFalse() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 0, 0, 0);
        Calendar previousMonth = calendarOf(2026, Calendar.FEBRUARY, 28, 15, 0, 0, 0);

        assertFalse(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_MONTHLY,
                previousMonth.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_monthlyTask_exactlyAtMonthStart_returnsTrue() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 0, 0, 0);
        Calendar startOfMonth = calendarOf(2026, Calendar.MARCH, 1, 0, 0, 0, 0);

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_MONTHLY,
                startOfMonth.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_monthlyTask_oneMillisecondBeforeMonthStart_returnsFalse() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 0, 0, 0);
        Calendar startOfMonth = calendarOf(2026, Calendar.MARCH, 1, 0, 0, 0, 0);

        assertFalse(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                Task.FREQUENCY_MONTHLY,
                startOfMonth.getTimeInMillis() - 1L,
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_emojiFrequency_sameDay_returnsTrue() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 0, 0, 0);
        Calendar earlierToday = calendarOf(2026, Calendar.MARCH, 29, 9, 0, 0, 0);

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                "Diaria 🔄",
                earlierToday.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void wasCompletedInCurrentPeriod_englishFrequency_sameDay_returnsTrue() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 0, 0, 0);
        Calendar earlierToday = calendarOf(2026, Calendar.MARCH, 29, 9, 0, 0, 0);

        assertTrue(TaskRecurrenceUtils.wasCompletedInCurrentPeriod(
                "Daily",
                earlierToday.getTimeInMillis(),
                now.getTimeInMillis()
        ));
    }

    @Test
    public void getCurrentPeriodStart_daily_returnsStartOfSameDay() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 45, 30, 123);

        long periodStart = TaskRecurrenceUtils.getCurrentPeriodStart(
                Task.FREQUENCY_DAILY,
                now.getTimeInMillis()
        );

        Calendar expected = calendarOf(2026, Calendar.MARCH, 29, 0, 0, 0, 0);

        assertEquals(expected.getTimeInMillis(), periodStart);
    }

    @Test
    public void getCurrentPeriodStart_weekly_returnsStartOfCurrentWeek() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 45, 30, 123); // Domingo

        long periodStart = TaskRecurrenceUtils.getCurrentPeriodStart(
                Task.FREQUENCY_WEEKLY,
                now.getTimeInMillis()
        );

        Calendar expected = calendarOf(2026, Calendar.MARCH, 23, 0, 0, 0, 0); // Lunes

        assertEquals(expected.getTimeInMillis(), periodStart);
    }

    @Test
    public void getCurrentPeriodStart_weekly_whenNowIsMonday_returnsSameMonday() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 30, 10, 15, 0, 0); // Lunes

        long periodStart = TaskRecurrenceUtils.getCurrentPeriodStart(
                Task.FREQUENCY_WEEKLY,
                now.getTimeInMillis()
        );

        Calendar expected = calendarOf(2026, Calendar.MARCH, 30, 0, 0, 0, 0); // Ese mismo lunes

        assertEquals(expected.getTimeInMillis(), periodStart);
    }

    @Test
    public void getCurrentPeriodStart_weekly_whenNowIsSunday_returnsMondayOfSameWeek() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 0, 0, 0); // Domingo

        long periodStart = TaskRecurrenceUtils.getCurrentPeriodStart(
                Task.FREQUENCY_WEEKLY,
                now.getTimeInMillis()
        );

        Calendar expected = calendarOf(2026, Calendar.MARCH, 23, 0, 0, 0, 0); // Lunes de esa semana

        assertEquals(expected.getTimeInMillis(), periodStart);
    }

    @Test
    public void getCurrentPeriodStart_monthly_returnsFirstDayOfMonthAtMidnight() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 45, 30, 123);

        long periodStart = TaskRecurrenceUtils.getCurrentPeriodStart(
                Task.FREQUENCY_MONTHLY,
                now.getTimeInMillis()
        );

        Calendar expected = calendarOf(2026, Calendar.MARCH, 1, 0, 0, 0, 0);

        assertEquals(expected.getTimeInMillis(), periodStart);
    }

    @Test
    public void getCurrentPeriodStart_once_returnsZero() {
        Calendar now = calendarOf(2026, Calendar.MARCH, 29, 15, 45, 30, 123);

        long periodStart = TaskRecurrenceUtils.getCurrentPeriodStart(
                Task.FREQUENCY_ONCE,
                now.getTimeInMillis()
        );

        assertEquals(0L, periodStart);
    }

    private Calendar calendarOf(int year, int month, int day, int hour, int minute, int second, int millis) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute, second);
        cal.set(Calendar.MILLISECOND, millis);
        return cal;
    }
}