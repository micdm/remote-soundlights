package com.micdm.remotesoundlights.activities;

import android.os.Bundle;
import android.view.Display;

import com.micdm.remotesoundlights.activities.boss.Analyzer;
import com.micdm.remotesoundlights.data.GainListPacket;
import com.micdm.remotesoundlights.data.GainListPacketBuilder;
import com.micdm.remotesoundlights.net.NetParams;
import com.micdm.remotesoundlights.net.ReceiverThread;
import com.micdm.remotesoundlights.utils.Logger;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.AverageFPSCounter;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import java.io.IOException;

public abstract class VisualizationActivity extends SimpleBaseGameActivity {

    private interface Visualizer {
        public IUpdateHandler getSpriteHandler();
        public void visualize(Analyzer.Gain[] gains);
    }

    private class CircleVisualizer implements Visualizer {

        private TextureRegion getCircleSpriteRegion() {
            try {
                BitmapTexture texture = (BitmapTexture) getTextureManager().getTexture("circle", getAssets(), "gfx/circle.png");
                return TextureRegionFactory.extractFromTexture(texture);
            } catch (IOException e) {
                return null;
            }
        }

        private void addCircleSprite(float size, Color color) {
            Camera camera = getEngine().getCamera();
            if (camera == null) {
                return;
            }
            TextureRegion region = getCircleSpriteRegion();
            if (region == null) {
                return;
            }
            Scene scene = getEngine().getScene();
            if (scene == null) {
                return;
            }
            float x = (float) (Math.random() * (camera.getWidth() - size));
            float y = (float) (Math.random() * (camera.getHeight() - size));
            Sprite sprite = new Sprite(x, y, size, size, region, getVertexBufferObjectManager());
            sprite.setRotationCenter(size / 2, size / 2);
            sprite.setColor(color);
            scene.attachChild(sprite);
        }

        private float getCircleSize(Analyzer.LEVEL level) {
            Camera camera = getEngine().getCamera();
            int number = level.getNumber();
            if (number >= 5) {
                return camera.getWidth() / 10;
            }
            if (number >= 2) {
                return camera.getHeight() / 2;
            }
            return camera.getWidth() * 1.5f;
        }

        private Color getCircleColor(Analyzer.LEVEL level) {
            int number = level.getNumber();
            if (number >= 5) {
                return new Color(1, 1, 1);
            }
            if (number >= 2) {
                return new Color(0, 1, 0);
            }
            return new Color(1, 0, 0);
        }

        @Override
        public void visualize(Analyzer.Gain[] gains) {
            for (Analyzer.Gain gain: gains) {
                Analyzer.LEVEL level = gain.getLevel();
                float size = getCircleSize(level);
                Color color = getCircleColor(level);
                addCircleSprite(size, color);
            }
        }

        @Override
        public IUpdateHandler getSpriteHandler() {
            return new IUpdateHandler() {

                private final float ALPHA_PER_SECOND = 2;
                private final float PIXEL_PER_SECOND = 200;
                private final float DEGREES_PER_SECOND = 180;

                @Override
                public void onUpdate(float elapsed) {
                    Scene scene = getEngine().getScene();
                    float alpha = elapsed * ALPHA_PER_SECOND;
                    float size = elapsed * PIXEL_PER_SECOND;
                    float degrees = elapsed * DEGREES_PER_SECOND;
                    for (int i = scene.getChildCount() - 1; i >= 0; i -= 1) {
                        Sprite sprite = (Sprite) scene.getChildByIndex(i);
                        sprite.setAlpha(sprite.getAlpha() - alpha);
                        if (sprite.getAlpha() < 0.01) {
                            scene.detachChild(sprite);
                            continue;
                        }
                        sprite.setX(sprite.getX() - size / 2);
                        sprite.setWidth(sprite.getWidth() + size);
                        sprite.setY(sprite.getY() - size / 2);
                        sprite.setHeight(sprite.getHeight() + size);
                        sprite.setRotation(sprite.getRotation() + degrees);
                    }
                }

                @Override
                public void reset() {}
            };
        }
    }

    private class BarVisualizer implements Visualizer {

        private final int BAR_COUNT = 7;
        private final float MAX_SIZE = 255;

        private TextureRegion getBarSpriteRegion() {
            try {
                BitmapTexture texture = (BitmapTexture) getTextureManager().getTexture("bar", getAssets(), "gfx/bar.png");
                return TextureRegionFactory.extractFromTexture(texture);
            } catch (IOException e) {
                return null;
            }
        }

        private Sprite getBarSprite(int number) {
            Scene scene = getEngine().getScene();
            if (scene == null) {
                return null;
            }
            if (scene.getChildCount() == 0) {
                Camera camera = getEngine().getCamera();
                float width = camera.getWidth() / BAR_COUNT;
                TextureRegion region = getBarSpriteRegion();
                for (int i = 0; i < BAR_COUNT; i += 1) {
                    float x = width * i;
                    Sprite sprite = new Sprite(x, 0, width, 0, region, getVertexBufferObjectManager());
                    scene.attachChild(sprite);
                }
            }
            return (Sprite) scene.getChildByIndex(number);
        }

        private void setBarSpriteSize(int number, float size) {
            Sprite sprite = getBarSprite(number);
            if (sprite == null) {
                return;
            }
            Camera camera = getEngine().getCamera();
            sprite.setHeight(camera.getHeight() / MAX_SIZE * size);
        }

        @Override
        public void visualize(Analyzer.Gain[] gains) {
            for (Analyzer.Gain gain: gains) {
                setBarSpriteSize(gain.getLevel().getNumber(), gain.getValue());
            }
        }

        @Override
        public IUpdateHandler getSpriteHandler() {
            return new IUpdateHandler() {

                private final float PIXEL_PER_SECOND = 100;

                @Override
                public void onUpdate(float elapsed) {
                    Scene scene = getEngine().getScene();
                    float size = elapsed * PIXEL_PER_SECOND;
                    for (int i = scene.getChildCount() - 1; i >= 0; i -= 1) {
                        Sprite sprite = (Sprite) scene.getChildByIndex(i);
                        sprite.setHeight(Math.max(sprite.getHeight() - size, 0));
                    }
                }

                @Override
                public void reset() {}
            };
        }
    }

    private Visualizer Visualizer;
    private ReceiverThread receiver;

    private void setupVisualizer() {
        Visualizer = new CircleVisualizer();
    }

    private void setupReceiver() {
        receiver = new ReceiverThread(NetParams.PORT, new ReceiverThread.OnDataListener() {
            @Override
            public void onData(byte[] data) {
                final GainListPacket packet = GainListPacketBuilder.decode(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Visualizer.visualize(packet.getGains());
                    }
                });
            }
        });
        receiver.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupVisualizer();
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
        getEngine().registerUpdateHandler(Visualizer.getSpriteHandler());
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
