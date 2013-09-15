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
import com.micdm.remotesoundlights.utils.RateMessage;
import com.micdm.remotesoundlights.utils.ResourceRegistry;
import com.micdm.remotesoundlights.visualizers.PointVisualizer;
import com.micdm.remotesoundlights.visualizers.Visualizer;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class VisualizationActivity extends SimpleBaseGameActivity {

    private Visualizer visualizer;
    private BaseMode mode;

    private void showRateMessage() {
        RateMessage message = new RateMessage(this);
        message.update();
        message.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        visualizer = new PointVisualizer(this, getEngine());
        showRateMessage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mode != null) {
            mode.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    private BaseMode.OnReceiveListener getReceiveListener() {
        return new BaseMode.OnReceiveListener() {
            @Override
            public void onReceive(final PeakListPacket packet) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        visualizer.visualize(packet.getPeaks());
                    }
                });
            }
        };
    }

    private boolean checkIfWifiEnabled() {
        WifiManager manager = (WifiManager) getSystemService(WIFI_SERVICE);
        return manager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
    }

    private void showWiFiDisabledMessage() {
        Toast message = Toast.makeText(this, R.string.wifi_disabled_message, Toast.LENGTH_LONG);
        message.show();
    }

    private void setupMode(SelectModeSceneBuilder.ModeType type) {
        if (type == SelectModeSceneBuilder.ModeType.GUEST) {
            mode = new BaseMode(this, getReceiveListener());
        }
        if (type == SelectModeSceneBuilder.ModeType.BOSS) {
            mode = new BossMode(this, getReceiveListener());
        }
        mode.onCreate();
        mode.onStart();
    }

    private Scene buildSelectModeScene() {
        SelectModeSceneBuilder builder = new SelectModeSceneBuilder(this, getEngine(), new SelectModeSceneBuilder.OnSelectModeListener() {
            @Override
            public void onSelectMode(SelectModeSceneBuilder.ModeType type) {
                if (checkIfWifiEnabled()) {
                    getEngine().setScene(buildVisualizationScene());
                    setupMode(type);
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
        return builder.build();
    }

    private Scene buildVisualizationScene() {
        VisualizationSceneBuilder builder = new VisualizationSceneBuilder(visualizer);
        return builder.build();
    }

    @Override
    protected Scene onCreateScene() {
        return buildSelectModeScene();
    }
}
