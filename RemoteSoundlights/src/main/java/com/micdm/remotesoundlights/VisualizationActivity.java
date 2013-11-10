package com.micdm.remotesoundlights;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Display;
import android.widget.Toast;

import com.micdm.remotesoundlights.data.PeakListPacket;
import com.micdm.remotesoundlights.modes.BaseMode;
import com.micdm.remotesoundlights.modes.boss.BossMode;
import com.micdm.remotesoundlights.scenes.SelectModeSceneBuilder;
import com.micdm.remotesoundlights.scenes.VisualizationSceneBuilder;
import com.micdm.remotesoundlights.utils.AnalyticsTracker;
import com.micdm.remotesoundlights.utils.RateMessage;
import com.micdm.remotesoundlights.utils.ResourceRegistry;
import com.micdm.remotesoundlights.visualizers.FlashlightVisualizer;
import com.micdm.remotesoundlights.visualizers.PointVisualizer;
import com.micdm.remotesoundlights.visualizers.SpriteVisualizer;
import com.micdm.remotesoundlights.visualizers.Visualizer;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import java.util.ArrayList;

public class VisualizationActivity extends SimpleBaseGameActivity {

    private ArrayList<Visualizer> visualizers = new ArrayList<Visualizer>();
    private BaseMode mode;

    private void showRateMessage() {
        RateMessage message = new RateMessage(this);
        message.update();
        message.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showRateMessage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AnalyticsTracker.sendActivityStart(this);
        if (mode != null) {
            mode.onStart();
        }
        for (Visualizer visualizer: visualizers) {
            visualizer.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        AnalyticsTracker.sendActivityStop(this);
        for (Visualizer visualizer: visualizers) {
            visualizer.stop();
        }
        if (mode != null) {
            mode.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mode != null) {
            mode.onDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (getEngine().getScene() instanceof VisualizationSceneBuilder.Scene) {
            mode.onStop();
            mode.onDestroy();
            mode = null;
            visualizers.clear();
            getEngine().setScene(buildSelectModeScene());
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        Display display = getWindowManager().getDefaultDisplay();
        Camera camera = new Camera(0, 0, display.getWidth(), display.getHeight());
        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
    }

    @Override
    protected void onCreateResources() {
        ResourceRegistry.load(this, getEngine());
    }

    private boolean checkIfWifiEnabled() {
        WifiManager manager = (WifiManager) getSystemService(WIFI_SERVICE);
        return manager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
    }

    private void showWiFiDisabledMessage() {
        Toast message = Toast.makeText(this, R.string.wifi_disabled_message, Toast.LENGTH_LONG);
        message.show();
    }

    private void setupVisualizers() {
        visualizers.add(new PointVisualizer(this, getEngine()));
        if (FlashlightVisualizer.isAvailable(this)) {
            visualizers.add(new FlashlightVisualizer());
        }
    }

    private BaseMode.OnReceiveListener getReceiveListener() {
        return new BaseMode.OnReceiveListener() {
            @Override
            public void onReceive(final PeakListPacket packet) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (Visualizer visualizer: visualizers) {
                            visualizer.visualize(packet.getPeaks());
                        }
                    }
                });
            }
        };
    }

    private void setupMode(SelectModeSceneBuilder.ModeType type) {
        if (type == SelectModeSceneBuilder.ModeType.GUEST) {
            mode = new BaseMode(this, getReceiveListener());
            AnalyticsTracker.sendEvent(this, "mode", "select", "guest");
        }
        if (type == SelectModeSceneBuilder.ModeType.BOSS) {
            mode = new BossMode(this, getReceiveListener());
            AnalyticsTracker.sendEvent(this, "mode", "select", "boss");
        }
        mode.onCreate();
        mode.onStart();
    }

    private Scene buildSelectModeScene() {
        SelectModeSceneBuilder builder = new SelectModeSceneBuilder(this, getEngine());
        return builder.build(new SelectModeSceneBuilder.OnSelectModeListener() {
            @Override
            public void onSelectMode(SelectModeSceneBuilder.ModeType type) {
                if (checkIfWifiEnabled()) {
                    setupVisualizers();
                    getEngine().setScene(buildVisualizationScene(type));
                    setupMode(type);
                    for (Visualizer visualizer: visualizers) {
                        visualizer.start();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWiFiDisabledMessage();
                        }
                    });
                }
            }
        });
    }

    private SpriteVisualizer getSpriteVisualizer() {
        for (Visualizer visualizer: visualizers) {
            if (visualizer instanceof SpriteVisualizer) {
                return (SpriteVisualizer) visualizer;
            }
        }
        throw new RuntimeException("No sprite visualizer found");
    }

    private Scene buildVisualizationScene(SelectModeSceneBuilder.ModeType type) {
        VisualizationSceneBuilder builder = new VisualizationSceneBuilder(this, getEngine());
        return builder.build(getSpriteVisualizer(), type);
    }

    @Override
    protected Scene onCreateScene() {
        return buildSelectModeScene();
    }
}
