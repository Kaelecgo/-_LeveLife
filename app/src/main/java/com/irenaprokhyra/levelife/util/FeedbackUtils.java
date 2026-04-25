package com.irenaprokhyra.levelife.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.HapticFeedbackConstants;
import android.view.View;

import com.irenaprokhyra.levelife.R;

import java.util.HashMap;
import java.util.Map;

public final class FeedbackUtils {

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private static SoundPool soundPool;
    private static final Map<Integer, Integer> soundIdsByRes = new HashMap<>();
    private static int loadedSoundsCount;
    private static boolean soundsLoaded;

    private FeedbackUtils() {
    }

    public static void playTaskCompletedFeedback(View anchorView) {
        safeRun(() -> {
            performHaptic(anchorView, HapticFeedbackConstants.KEYBOARD_TAP);
            vibrate(anchorView != null ? anchorView.getContext() : null, 28);
            playSound(anchorView != null ? anchorView.getContext() : null, R.raw.sfx_task_complete, 0.95f);
        });
    }

    public static void playPurchaseFeedback(View anchorView) {
        safeRun(() -> {
            performHaptic(anchorView, HapticFeedbackConstants.KEYBOARD_TAP);
            vibrate(anchorView != null ? anchorView.getContext() : null, 22);
            playSound(anchorView != null ? anchorView.getContext() : null, R.raw.sfx_purchase, 0.95f);
        });
    }

    public static void playLevelUpFeedback(View anchorView) {
        safeRun(() -> {
            performHaptic(anchorView, HapticFeedbackConstants.LONG_PRESS);
            vibrate(anchorView != null ? anchorView.getContext() : null, 45);
            playSound(anchorView != null ? anchorView.getContext() : null, R.raw.sfx_level_up, 1.0f);
        });
    }

    private static void performHaptic(View anchorView, int constant) {
        if (anchorView != null) {
            anchorView.performHapticFeedback(constant);
        }
    }

    private static void vibrate(Context context, int durationMs) {
        if (context == null) {
            return;
        }

        Vibrator vibrator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = context.getSystemService(VibratorManager.class);
            vibrator = vibratorManager != null ? vibratorManager.getDefaultVibrator() : null;
        } else {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }

        if (vibrator == null || !vibrator.hasVibrator()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(durationMs);
        }
    }

    private static synchronized void playSound(Context context, int soundResId, float volume) {
        if (context == null) {
            return;
        }

        ensureSoundPool(context.getApplicationContext());
        Integer soundId = soundIdsByRes.get(soundResId);
        if (soundId == null) {
            return;
        }

        if (!soundsLoaded) {
            MAIN_HANDLER.postDelayed(() -> safeRun(() -> playSound(context.getApplicationContext(), soundResId, volume)), 90);
            return;
        }

        soundPool.play(soundId, volume, volume, 1, 0, 1.0f);
    }

    private static synchronized void ensureSoundPool(Context appContext) {
        if (soundPool != null) {
            return;
        }

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(3)
                .build();

        soundPool.setOnLoadCompleteListener((pool, sampleId, status) -> {
            if (status == 0) {
                loadedSoundsCount++;
                soundsLoaded = loadedSoundsCount >= 3;
            }
        });

        soundIdsByRes.put(R.raw.sfx_task_complete, soundPool.load(appContext, R.raw.sfx_task_complete, 1));
        soundIdsByRes.put(R.raw.sfx_purchase, soundPool.load(appContext, R.raw.sfx_purchase, 1));
        soundIdsByRes.put(R.raw.sfx_level_up, soundPool.load(appContext, R.raw.sfx_level_up, 1));
    }

    private static void safeRun(Runnable action) {
        try {
            action.run();
        } catch (Throwable ignored) {
            // Feedback should never break the main app flow.
        }
    }
}
