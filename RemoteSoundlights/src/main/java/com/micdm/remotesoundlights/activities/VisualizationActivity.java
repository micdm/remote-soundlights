package com.micdm.remotesoundlights.activities;

import android.os.Bundle;
import android.view.Display;

import com.micdm.remotesoundlights.data.GainListPacket;
import com.micdm.remotesoundlights.data.GainListPacketBuilder;
import com.micdm.remotesoundlights.net.ReceiverThread;
import com.micdm.remotesoundlights.utils.Logger;
import com.micdm.remotesoundlights.visualizers.PointVisualizer;
import com.micdm.remotesoundlights.visualizers.Visualizer;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.AverageFPSCounter;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public abstract class VisualizationActivity extends SimpleBaseGameActivity {

    private Visualizer visualizer;
    private ReceiverThread receiver;

    private void setupReceiver() {
        receiver = new ReceiverThread(new ReceiverThread.OnDataListener() {
            @Override
            public void onData(byte[] data) {
                final GainListPacket packet = GainListPacketBuilder.decode(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        visualizer.visualize(packet.getGains());
                    }
                });
            }
        });
        receiver.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        visualizer = new PointVisualizer(mEngine, getAssets());
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupReceiver();
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
        Scene scene = new Scene();
        scene.setBackground(new Background(0, 0, 0));
        getEngine().registerUpdateHandler(visualizer.getSpriteHandler());
        getEngine().registerUpdateHandler(new AverageFPSCounter() {
            @Override
            protected void onHandleAverageDurationElapsed(float fps) {
                Logger.debug("FPS is " + fps + " now");
            }
        });
        return scene;
    }

    @Override
    protected void onStop() {
        super.onStop();
        receiver.cancel();
    }
}
