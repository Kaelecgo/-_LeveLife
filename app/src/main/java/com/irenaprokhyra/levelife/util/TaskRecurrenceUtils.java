package com.irenaprokhyra.levelife.util;

import com.irenaprokhyra.levelife.model.Task;

import java.util.Calendar;

public final class TaskRecurrenceUtils {

    private TaskRecurrenceUtils() {
    }

    public static boolean isCompletedForCurrentPeriod(Task task) {
        if (task == null) {
            return false;
        }

        if (!task.isRecurring()) {
            return task.isCompleted();
        }

        return wasCompletedInCurrentPeriod(task.getFrequency(), task.getLastCompletedAt(), System.currentTimeMillis());
    }

    public static boolean wasCompletedInCurrentPeriod(String frequency, long lastCompletedAt, long now) {
        if (lastCompletedAt <= 0L) {
            return false;
        }

        String normalizedFrequency = Task.normalizeFrequency(frequency);
        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.setTimeInMillis(lastCompletedAt);

        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTimeInMillis(now);

        if (Task.FREQUENCY_DAILY.equals(normalizedFrequency)) {
            return sameDay(lastCalendar, nowCalendar);
        }

        if (Task.FREQUENCY_WEEKLY.equals(normalizedFrequency)) {
            return lastCalendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR)
                    && lastCalendar.get(Calendar.WEEK_OF_YEAR) == nowCalendar.get(Calendar.WEEK_OF_YEAR);
        }

        if (Task.FREQUENCY_MONTHLY.equals(normalizedFrequency)) {
            return lastCalendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR)
                    && lastCalendar.get(Calendar.MONTH) == nowCalendar.get(Calendar.MONTH);
        }

        return false;
    }

    public static String getFrequencyLabel(Task task) {
        if (task == null || task.getFrequency() == null) {
            return Task.FREQUENCY_ONCE;
        }
        return Task.normalizeFrequency(task.getFrequency());
    }

    private static boolean sameDay(Calendar first, Calendar second) {
        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR)
                && first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR);
    }
}
