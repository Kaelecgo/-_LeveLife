package com.irenaprokhyra.levelife.util;

import android.content.Context;
import android.content.SharedPreferences;

public final class SessionManager {
    private static final String PREFS_NAME = "LeveLifeSession";
    private static final String KEY_SAVED_USER_ID = "saved_user_id";

    private SessionManager() {
    }

    public static int getSavedUserId(Context context) {
        return getPreferences(context).getInt(KEY_SAVED_USER_ID, -1);
    }

    public static void saveSession(Context context, int userId) {
        getPreferences(context)
                .edit()
                .putInt(KEY_SAVED_USER_ID, userId)
                .apply();
    }

    public static void clearSession(Context context) {
        getPreferences(context)
                .edit()
                .clear()
                .apply();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
