package com.micdm.remotesoundlights;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Display;
import android.widget.Toast;

import com.micdm.remotesoundlights.data.PeakListPacket;
import com.micdm.remotesoundlights.modes.BaseMode;
import com.micdm.remotesoundlights.modes.boss.BossMode;
import com.micdm.remotesoundlights.notifiers.NewsNotifier;
import com.micdm.remotesoundlights.notifiers.RateNotifier;
import com.micdm.remotesoundlights.scene_builders.LoadingSceneBuilder;
import com.micdm.remotesoundlights.scene_builders.SelectModeSceneBuilder;
import com.micdm.remotesoundlights.scene_builders.VisualizationSceneBuilder;
import com.micdm.remotesoundlights.utils.AnalyticsTracker;
import com.micdm.remotesoundlights.utils.ResourceRegistry;
import com.micdm.remotesoundlights.visualizers.FlashlightVisualizer;
import com.micdm.remotesoundlights.visualizers.PointVisualizer;
import com.micdm.remotesoundlights.visualizers.SpriteVisualizer;
import com.micdm.remotesoundlights.visualizers.Visualizer;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import java.util.ArrayList;

public class VisualizationActivity extends SimpleBaseGameActivity {

    private ArrayList<Visualizer> visualizers = new ArrayList<Visualizer>();
    private BaseMode mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showMessages();
    }

    private void showMessages() {
        RateNotifier.update(this);
        if (!NewsNotifier.show(this)) {
            RateNotifier.show(this);
        }
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
            getEngine().setScene(buildLoadingScene());
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
    protected void onCreateResources() {}

    @Override
    protected Scene onCreateScene() {
        return buildLoadingScene();
    }

    private Scene buildLoadingScene() {
        ResourceRegistry.loadMinimal(this, getEngine());
        Scene scene = LoadingSceneBuilder.build(this, getEngine());
        scene.registerUpdateHandler(new TimerHandler(0.001f, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler timer) {
                getEngine().setScene(buildSelectModeScene());
            }
        }));
        return scene;
    }

    private Scene buildSelectModeScene() {
        ResourceRegistry.load(this, getEngine());
        return SelectModeSceneBuilder.build(this, getEngine(), new SelectModeSceneBuilder.OnSelectModeListener() {
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
        if (FlashlightVisualizer.canBeEnabled(this)) {
            visualizers.add(new FlashlightVisualizer(this));
        }
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

    private BaseMode.OnReceiveListener getReceiveListener() {
        return new BaseMode.OnReceiveListener() {

            private boolean isFirstPacket = true;

            @Override
            public void onReceive(final PeakListPacket packet) {
                if (isFirstPacket) {
                    VisualizationSceneBuilder.prepare((VisualizationSceneBuilder.Scene) getEngine().getScene());
                    isFirstPacket = false;
                }
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

    private Scene buildVisualizationScene(SelectModeSceneBuilder.ModeType type) {
        Scene scene = VisualizationSceneBuilder.build(this, getEngine(), type, new VisualizationSceneBuilder.OnSettingsListener() {
            @Override
            public void onSettings() {
                goToSettings();
            }
        });
        scene.registerUpdateHandler(getSpriteVisualizer().getSpriteHandler());
        return scene;
    }

    private SpriteVisualizer getSpriteVisualizer() {
        for (Visualizer visualizer: visualizers) {
            if (visualizer instanceof SpriteVisualizer) {
                return (SpriteVisualizer) visualizer;
            }
        }
        throw new RuntimeException("No sprite visualizer found");
    }

    private void goToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
