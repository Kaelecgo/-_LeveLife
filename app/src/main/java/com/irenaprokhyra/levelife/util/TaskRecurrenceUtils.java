package com.irenaprokhyra.levelife.util;

import com.irenaprokhyra.levelife.model.Task;
import java.util.Calendar;

public final class TaskRecurrenceUtils {

    private TaskRecurrenceUtils() {}

    /**
     * Determina si una tarea está completada para el periodo actual basándose en su caché
     * 'lastCompletedAt'. Ideal para uso rápido en la UI (Adapters).
     */
    public static boolean isCompletedForCurrentPeriod(Task task) {
        if (task == null) return false;
        if (!task.isRecurring()) return task.isCompleted();

        long periodStart = getCurrentPeriodStart(task.getFrequency(), System.currentTimeMillis());
        return task.getLastCompletedAt() >= periodStart;
    }

    /**
     * Calcula el timestamp (ms) exacto en el que comenzó el periodo actual
     * (día, semana o mes) para una frecuencia dada.
     */
    public static long getCurrentPeriodStart(String frequency, long now) {
        String normalized = Task.normalizeFrequency(frequency);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now);

        // Resetear siempre a las 00:00:00.000
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (Task.FREQUENCY_DAILY.equals(normalized)) {
            return cal.getTimeInMillis();
        }

        if (Task.FREQUENCY_WEEKLY.equals(normalized)) {
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            return cal.getTimeInMillis();
        }

        if (Task.FREQUENCY_MONTHLY.equals(normalized)) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            return cal.getTimeInMillis();
        }

        return 0L;
    }

    public static String getFrequencyLabel(Task task) {
        if (task == null || task.getFrequency() == null) {
            return Task.FREQUENCY_ONCE;
        }
        return Task.normalizeFrequency(task.getFrequency());
    }
}
