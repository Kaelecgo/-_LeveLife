package com.irenaprokhyra.levelife.util;

import com.irenaprokhyra.levelife.model.Task;
import java.util.Calendar;
import java.util.Locale;

public final class TaskRecurrenceUtils {

    private TaskRecurrenceUtils() {}

    public static boolean isCompletedForCurrentPeriod(Task task) {
        if (task == null) return false;
        if (!task.isRecurring()) return task.isCompleted();

        return wasCompletedInCurrentPeriod(task.getFrequency(), task.getLastCompletedAt(), System.currentTimeMillis());
    }

    public static boolean wasCompletedInCurrentPeriod(String frequency, long lastCompletedAt, long now) {
        long periodStart = getCurrentPeriodStart(frequency, now);
        if (periodStart == 0L) return false;
        return lastCompletedAt >= periodStart;
    }

    public static long getCurrentPeriodStart(String frequency, long now) {
        String normalized = Task.normalizeFrequency(frequency);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now);
        cal.setFirstDayOfWeek(Calendar.MONDAY);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (Task.FREQUENCY_DAILY.equals(normalized)) {
            return cal.getTimeInMillis();
        }

        if (Task.FREQUENCY_WEEKLY.equals(normalized)) {
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            if (cal.getTimeInMillis() > now) {
                cal.add(Calendar.WEEK_OF_YEAR, -1);
            }
            return cal.getTimeInMillis();
        }

        if (Task.FREQUENCY_MONTHLY.equals(normalized)) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            return cal.getTimeInMillis();
        }

        return 0L;
    }

    public static int getFrequencyPriority(String frequency) {
        String normalized = Task.normalizeFrequency(frequency);
        switch (normalized) {
            case Task.FREQUENCY_ONCE:
                return 1;
            case Task.FREQUENCY_DAILY:
                return 2;
            case Task.FREQUENCY_WEEKLY:
                return 3;
            case Task.FREQUENCY_MONTHLY:
                return 4;
            default:
                return 5;
        }
    }

    public static String getFrequencyLabel(Task task) {
        if (task == null || task.getFrequency() == null) {
            return Task.FREQUENCY_ONCE;
        }
        return Task.normalizeFrequency(task.getFrequency());
    }

    public static long getMillisUntilNextPeriod(String frequency, long now) {
        long periodStart = getCurrentPeriodStart(frequency, now);
        if (periodStart == 0L) return 0L;

        String normalized = Task.normalizeFrequency(frequency);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(periodStart);
        cal.setFirstDayOfWeek(Calendar.MONDAY);

        if (Task.FREQUENCY_DAILY.equals(normalized)) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        } else if (Task.FREQUENCY_WEEKLY.equals(normalized)) {
            cal.add(Calendar.WEEK_OF_YEAR, 1);
        } else if (Task.FREQUENCY_MONTHLY.equals(normalized)) {
            cal.add(Calendar.MONTH, 1);
        } else {
            return 0L;
        }

        return cal.getTimeInMillis() - now;
    }

    public static String formatRemainingTime(long millis) {
        if (millis <= 0) return "00:00:00";
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        long sec = seconds % 60;
        long min = minutes % 60;
        long hr = hours % 24;

        if (days > 0) {
            return String.format(Locale.getDefault(), "%dd %02d:%02d:%02d", days, hr, min, sec);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hr, min, sec);
        }
    }
}
