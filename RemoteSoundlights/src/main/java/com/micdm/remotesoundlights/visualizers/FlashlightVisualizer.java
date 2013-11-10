package com.micdm.remotesoundlights.visualizers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.micdm.remotesoundlights.flashlight.FlashlightController;
import com.micdm.remotesoundlights.modes.boss.Analyzer;

import java.util.Timer;
import java.util.TimerTask;

public class FlashlightVisualizer implements Visualizer {

    public static boolean canBeEnabled(Context context) {
        return isAvailable(context) && isEnabled(context);
    }

    private static final String PREF_KEY_USE = "settings_use_flashlight";
    private static final String PREF_KEY_BASS = "settings_use_flashlight_bass";
    private static final String PREF_KEY_MIDDLE = "settings_use_flashlight_middle";
    private static final String PREF_KEY_HIGH = "settings_use_flashlight_high";
    private static final int BLINK_DURATION = 10;

    private static boolean isAvailable(Context context) {
        PackageManager manager = context.getPackageManager();
        return manager != null && manager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private static boolean isEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PREF_KEY_USE, false);
    }

    private FlashlightController controller = new FlashlightController();
    private Timer timer;
    private boolean useOnBass;
    private boolean useOnMiddle;
    private boolean useOnHigh;

    public FlashlightVisualizer(Context context) {
        setupSwitches(context);
    }

    private void setupSwitches(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        useOnBass = prefs.getBoolean(PREF_KEY_BASS, false);
        useOnMiddle = prefs.getBoolean(PREF_KEY_MIDDLE, false);
        useOnHigh = prefs.getBoolean(PREF_KEY_HIGH, false);
    }

    @Override
    public void start() {
        controller.init();
        timer = new Timer();
    }

    @Override
    public void stop() {
        timer.cancel();
        controller.deactivate();
        controller.deinit();
    }

    @Override
    public void visualize(Analyzer.Peak[] peaks) {
        for (Analyzer.Peak peak: peaks) {
            Analyzer.LEVEL level = peak.getLevel();
            if ((useOnBass && level.isBass()) || (useOnMiddle && level.isMiddle()) || (useOnHigh && level.isHigh())) {
                blink();
            }
        }
    }

    private void blink() {
        if (!controller.isReady() || controller.isActive()) {
            return;
        }
        controller.activate();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                controller.deactivate();
            }
        }, BLINK_DURATION);
    }
}
