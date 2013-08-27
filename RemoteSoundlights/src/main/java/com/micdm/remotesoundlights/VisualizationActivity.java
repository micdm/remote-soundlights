package com.micdm.remotesoundlights;

import android.os.Bundle;
import android.view.Display;

import com.micdm.remotesoundlights.data.GainListPacket;
import com.micdm.remotesoundlights.modes.BaseMode;
import com.micdm.remotesoundlights.modes.boss.BossMode;
import com.micdm.remotesoundlights.scenes.SelectModeSceneBuilder;
import com.micdm.remotesoundlights.scenes.VisualizationSceneBuilder;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        visualizer = new PointVisualizer(mEngine, getAssets());
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        Display display = getWindowManager().getDefaultDisplay();
        Camera camera = new Camera(0, 0, display.getWidth(), display.getHeight());
        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
    }

    @Override
    protected void onCreateResources() {}

    private BaseMode.OnReceiveListener getReceiveListener() {
        return new BaseMode.OnReceiveListener() {
            @Override
            public void onReceive(final GainListPacket packet) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        visualizer.visualize(packet.getGains());
                    }
                });
            }
        };
    }

    private void setupMode(SelectModeSceneBuilder.ModeType type) {
        if (type == SelectModeSceneBuilder.ModeType.GUEST) {
            mode = new BaseMode(this, getReceiveListener());
        }
        if (type == SelectModeSceneBuilder.ModeType.BOSS) {
            mode = new BossMode(this, getReceiveListener());
        }
        mode.onCreate();
    }

    private Scene buildSelectModeScene() {
        SelectModeSceneBuilder builder = new SelectModeSceneBuilder(getEngine(), getAssets(), new SelectModeSceneBuilder.OnSelectModeListener() {
            @Override
            public void onSelectMode(SelectModeSceneBuilder.ModeType type) {
                getEngine().setScene(buildVisualizationScene());
                setupMode(type);
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
}
