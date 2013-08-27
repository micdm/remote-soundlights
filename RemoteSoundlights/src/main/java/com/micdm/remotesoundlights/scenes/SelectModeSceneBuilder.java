package com.micdm.remotesoundlights.scenes;

import android.content.res.AssetManager;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.adt.io.in.AssetInputStreamOpener;
import org.andengine.util.color.Color;

import java.io.IOException;

public class SelectModeSceneBuilder {

    public static interface OnSelectModeListener {
        public void onSelectMode(ModeType type);
    }

    public static enum ModeType {
        GUEST,
        BOSS
    }

    private Engine engine;
    private AssetManager assets;
    private OnSelectModeListener listener;

    public SelectModeSceneBuilder(Engine engine, AssetManager assets, OnSelectModeListener listener) {
        this.engine = engine;
        this.assets = assets;
        this.listener = listener;
    }

    private TextureRegion getTextureRegion() {
        try {
            TextureManager manager = engine.getTextureManager();
            AssetInputStreamOpener opener = new AssetInputStreamOpener(assets, "gfx/star.png");
            BitmapTexture texture = (BitmapTexture) manager.getTexture("point", opener, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR);
            return TextureRegionFactory.extractFromTexture(texture);
        } catch (IOException e) {
            throw new RuntimeException("Can not load texture");
        }
    }

    private void addBossButton(Scene scene) {
        float x = engine.getCamera().getCenterX() - 400;
        float y = engine.getCamera().getCenterY() - 200;
        TextureRegion region = getTextureRegion();
        Sprite sprite = new Sprite(x, y, 400, 400, region, engine.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent event, float x, float y) {
                if (event.getAction() == TouchEvent.ACTION_UP) {
                    listener.onSelectMode(ModeType.BOSS);
                    return true;
                }
                return false;
            }
        };
        sprite.setColor(1, 0, 0);
        scene.registerTouchArea(sprite);
        scene.attachChild(sprite);
    }

    private void addGuestButton(Scene scene) {
        float x = engine.getCamera().getCenterX();
        float y = engine.getCamera().getCenterY() - 200;
        TextureRegion region = getTextureRegion();
        Sprite sprite = new Sprite(x, y, 400, 400, region, engine.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent event, float x, float y) {
                if (event.getAction() == TouchEvent.ACTION_UP) {
                    listener.onSelectMode(ModeType.GUEST);
                    return true;
                }
                return false;
            }
        };
        sprite.setColor(0, 0, 1);
        scene.registerTouchArea(sprite);
        scene.attachChild(sprite);
    }

    public Scene build() {
        Scene scene = new Scene();
        scene.setBackground(new Background(Color.BLACK));
        addBossButton(scene);
        addGuestButton(scene);
        return scene;
    }
}
