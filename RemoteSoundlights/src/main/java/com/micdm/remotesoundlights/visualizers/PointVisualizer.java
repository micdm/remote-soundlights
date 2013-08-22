package com.micdm.remotesoundlights.visualizers;

import android.content.res.AssetManager;

import com.micdm.remotesoundlights.activities.boss.Analyzer;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.adt.io.in.AssetInputStreamOpener;
import org.andengine.util.color.Color;
import org.andengine.util.color.ColorUtils;

import java.io.IOException;

public class PointVisualizer implements Visualizer {

    private class UpdateHandler implements IUpdateHandler {

        private final float ALPHA_PER_SECOND = 2;
        private final float PIXEL_PER_SECOND = 200;
        private final float DEGREES_PER_SECOND = 180;

        @Override
        public void onUpdate(float elapsed) {
            Scene scene = engine.getScene();
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
                sprite.setRotationCenter(sprite.getWidth() / 2, sprite.getHeight() / 2);
                sprite.setRotation(sprite.getRotation() + degrees);
            }
        }

        @Override
        public void reset() {}
    }

    private Engine engine;
    private AssetManager assets;

    public PointVisualizer(Engine engine, AssetManager assets) {
        this.engine = engine;
        this.assets = assets;
    }

    private TextureRegion getSpriteRegion() {
        try {
            TextureManager manager = engine.getTextureManager();
            AssetInputStreamOpener opener = new AssetInputStreamOpener(assets, "gfx/star.png");
            BitmapTexture texture = (BitmapTexture) manager.getTexture("point", opener, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR);
            return TextureRegionFactory.extractFromTexture(texture);
        } catch (IOException e) {
            throw new RuntimeException("Can not load texture");
        }
    }

    private void addSprite(float size, Color color) {
        Camera camera = engine.getCamera();
        if (camera == null) {
            return;
        }
        TextureRegion region = getSpriteRegion();
        if (region == null) {
            return;
        }
        Scene scene = engine.getScene();
        if (scene == null) {
            return;
        }
        float x = (float) (Math.random() * (camera.getWidth() - size));
        float y = (float) (Math.random() * (camera.getHeight() - size));
        Sprite sprite = new Sprite(x, y, size, size, region, engine.getVertexBufferObjectManager());
        sprite.setColor(color);
        scene.attachChild(sprite);
    }

    private float getSize(Analyzer.LEVEL level) {
        Camera camera = engine.getCamera();
        int number = level.getNumber();
        if (number >= 5) {
            return camera.getWidth() / 10;
        }
        if (number >= 2) {
            return camera.getHeight() / 2;
        }
        return camera.getWidth() * 1.5f;
    }

    private Color getColor(Analyzer.LEVEL level) {
        int number = level.getNumber();
        if (number >= 5) {
            return ColorUtils.convertARGBPackedIntToColor(0xFF2D7395);
        }
        if (number >= 2) {
            return ColorUtils.convertARGBPackedIntToColor(0xFF37952D);
        }
        return ColorUtils.convertARGBPackedIntToColor(0xFF952D2D);
    }

    @Override
    public void visualize(Analyzer.Gain[] gains) {
        for (Analyzer.Gain gain: gains) {
            Analyzer.LEVEL level = gain.getLevel();
            float size = getSize(level);
            Color color = getColor(level);
            addSprite(size, color);
        }
    }

    @Override
    public IUpdateHandler getSpriteHandler() {
        return new UpdateHandler();
    }
}
