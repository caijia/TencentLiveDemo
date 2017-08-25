package com.caijiatest.tencentlivedemo.playController.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.media.AudioManager;
import android.provider.Settings;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import java.util.Locale;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by cai.jia on 2017/7/4 0004
 */

public class ControllerUtil {

    public static int getVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public static int getMaxVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public static void setVolume(Context context, int volume) {
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = volume;
        if (volume < 0) {
            currentVolume = 0;
        }

        if (volume > maxVolume) {
            currentVolume = maxVolume;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_PLAY_SOUND);
    }

    public static
    @IntRange(from = 0, to = 255)
    int getScreenBrightness(Context context) {
        try {
            return Settings.System.getInt(
                    context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static
    @IntRange(from = 0, to = 255)
    int getActivityBrightness(Context context) {
        Activity activity = getActivity(context);
        if (activity == null) {
            return 0;
        }

        Window window = activity.getWindow();
        if (window == null) {
            return 0;
        }
        WindowManager.LayoutParams lp = window.getAttributes();
        if (lp.screenBrightness < 0 || lp.screenBrightness > 1) {
            return getScreenBrightness(context);

        } else {
            return (int) (lp.screenBrightness * 255);
        }
    }

    public static void setActivityBrightness(Context context,
                                             @IntRange(from = 0, to = 255) int brightness) {
        Activity activity = getActivity(context);
        if (activity == null) {
            return;
        }

        Window window = activity.getWindow();
        if (window == null) {
            return;
        }

        if (brightness < 0 || brightness > 255) {
            return;
        }
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness * (1f / 255f); //0~1
        window.setAttributes(lp);
    }

    public static int spToDp(Context context, float px) {
        return Math.round(px / context.getResources().getDisplayMetrics().density);
    }

    public static
    @Nullable
    Activity getActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return getActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public static String formatTime(int seconds) {
        return formatTime(false,seconds);
    }

    public static String formatTime(boolean hasSymbol, int seconds) {
        String symbol = seconds > 0 ? (hasSymbol ? "+" : "") : (seconds == 0 ? "" : "-");
        int h = seconds / 3600;
        int m = seconds % 3600 / 60;
        int s = seconds % 3600 % 60;
        if (h > 0) {
            return String.format(Locale.CHINESE, "%s%02d:%02d:%02d", symbol, h, m, s);

        } else {
            return String.format(Locale.CHINESE, "%s%02d:%02d", symbol, m, s);
        }
    }

    public static void toggleActionBarAndStatusBar(Context context, boolean fullScreen) {
        Activity activity = getActivity(context);
        if (activity == null) {
            return;
        }

        Window window = activity.getWindow();
        if (window == null) {
            return;
        }

        ActionBar supportActionBar = null;
        android.app.ActionBar actionBar = null;
        if (activity instanceof AppCompatActivity) {
            supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();

        } else if (activity instanceof FragmentActivity) {
            actionBar = activity.getActionBar();
        }

        if (fullScreen) {
            if (supportActionBar != null) {
                supportActionBar.hide();
            }

            if (actionBar != null) {
                actionBar.hide();
            }

            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } else {
            if (supportActionBar != null) {
                supportActionBar.show();
            }

            if (actionBar != null) {
                actionBar.show();
            }

            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}
