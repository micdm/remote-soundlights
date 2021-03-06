package com.micdm.remotesoundlights.visualizers;

import android.content.Context;

import com.micdm.remotesoundlights.R;
import com.micdm.remotesoundlights.modes.boss.Analyzer;
import com.micdm.remotesoundlights.utils.ResourceRegistry;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.color.ColorUtils;

public class PointVisualizer implements SpriteVisualizer {

    private static class UpdateHandler implements IUpdateHandler {

        private final float ALPHA_PER_SECOND = 2;
        private final float PIXEL_PER_SECOND = 500;
        private final float DEGREES_PER_SECOND = 180;

        private Engine engine;

        public UpdateHandler(Engine engine) {
            this.engine = engine;
        }

        @Override
        public void onUpdate(float elapsed) {
            Scene scene = engine.getScene();
            float fade = elapsed * ALPHA_PER_SECOND;
            float growth = elapsed * PIXEL_PER_SECOND;
            float angle = elapsed * DEGREES_PER_SECOND;
            for (int i = scene.getChildCount() - 1; i >= 0; i -= 1) {
                RectangularShape shape = (RectangularShape) scene.getChildByIndex(i);
                if (!(shape instanceof PointSprite)) {
                    continue;
                }
                float alpha = shape.getAlpha();
                if (alpha < fade) {
                    scene.detachChild(shape);
                    continue;
                }
                shape.setAlpha(alpha - fade);
                shape.setX(shape.getX() - growth / 2);
                shape.setWidth(shape.getWidth() + growth);
                shape.setY(shape.getY() - growth / 2);
                shape.setHeight(shape.getHeight() + growth);
                shape.setRotationCenter(shape.getWidth() / 2, shape.getHeight() / 2);
                float rotation = shape.getRotation();
                shape.setRotation((rotation > 0) ? rotation + angle : rotation - angle);
            }
        }

        @Override
        public void reset() {}
    }

    private static class PointSprite extends Sprite {

        public PointSprite(float x, float y, float width, float height, ITextureRegion region, VertexBufferObjectManager manager) {
            super(x, y, width, height, region, manager);
        }
    }

    private Context context;
    private Engine engine;

    public PointVisualizer(Context context, Engine engine) {
        this.context = context;
        this.engine = engine;
    }

    private float getSize(Analyzer.LEVEL level) {
        Camera camera = engine.getCamera();
        if (level == Analyzer.LEVEL.HIGH_HIGH) {
            return camera.getWidth() * 0.05f;
        }
        if (level == Analyzer.LEVEL.LOW_HIGH) {
            return camera.getWidth() * 0.075f;
        }
        if (level == Analyzer.LEVEL.HIGH_MIDDLE) {
            return camera.getHeight() * 0.25f;
        }
        if (level == Analyzer.LEVEL.MEDIUM_MIDDLE) {
            return camera.getHeight() * 0.33f;
        }
        if (level == Analyzer.LEVEL.LOW_MIDDLE) {
            return camera.getHeight() * 0.5f;
        }
        if (level == Analyzer.LEVEL.HIGH_BASS) {
            return camera.getHeight() * 1.2f;
        }
        if (level == Analyzer.LEVEL.LOW_BASS) {
            return camera.getWidth() * 1.5f;
        }
        throw new RuntimeException(String.format("Unknow level %s", level));
    }

    private Color getColor(Analyzer.LEVEL level) {
        if (level.isHigh()) {
            return ColorUtils.convertARGBPackedIntToColor(context.getResources().getColor(R.color.high));
        }
        if (level.isMiddle()) {
            return ColorUtils.convertARGBPackedIntToColor(context.getResources().getColor(R.color.middle));
        }
        if (level.isBass()) {
            return ColorUtils.convertARGBPackedIntToColor(context.getResources().getColor(R.color.bass));
        }
        throw new RuntimeException(String.format("Unknown level %s", level));
    }

    private void addSprite(float size, Color color) {
        Camera camera = engine.getCamera();
        if (camera == null) {
            return;
        }
        TextureRegion region = TextureRegionFactory.extractFromTexture(ResourceRegistry.getStarTexture());
        if (region == null) {
            return;
        }
        Scene scene = engine.getScene();
        if (scene == null) {
            return;
        }
        float x = (float) (Math.random() * (camera.getWidth() - size));
        float y = (float) (Math.random() * (camera.getHeight() - size));
        Sprite sprite = new PointSprite(x, y, size, size, region, engine.getVertexBufferObjectManager());
        sprite.setColor(color);
        sprite.setRotation((float) (-180 + Math.random() * 360));
        scene.attachChild(sprite);
    }

    @Override
    public IUpdateHandler getSpriteHandler() {
        return new UpdateHandler(engine);
    }

    @Override
    public void start() {}

    @Override
    public void stop() {}

    @Override
    public void visualize(Analyzer.Peak[] peaks) {
        for (Analyzer.Peak peak: peaks) {
            Analyzer.LEVEL level = peak.getLevel();
            float size = getSize(level);
            Color color = getColor(level);
            addSprite(size, color);
        }
    }
}
