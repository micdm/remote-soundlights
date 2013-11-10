package com.micdm.remotesoundlights.visualizers;

import android.content.Context;
import android.content.pm.PackageManager;

import com.micdm.remotesoundlights.flashlight.FlashlightController;
import com.micdm.remotesoundlights.modes.boss.Analyzer;

import java.util.Timer;
import java.util.TimerTask;

public class FlashlightVisualizer implements Visualizer {

    public static boolean isAvailable(Context context) {
        PackageManager manager = context.getPackageManager();
        return manager != null && manager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private static final int BLINK_DURATION = 10;

    private FlashlightController controller = new FlashlightController();
    private Timer timer;

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
        for (Analyzer.Peak peak : peaks) {
            if (peak.getLevel() == Analyzer.LEVEL.LOW_BASS || peak.getLevel() == Analyzer.LEVEL.HIGH_BASS) {
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
