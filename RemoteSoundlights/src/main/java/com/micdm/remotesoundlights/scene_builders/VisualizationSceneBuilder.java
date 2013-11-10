package com.micdm.remotesoundlights.scene_builders;

import android.content.Context;

import com.micdm.remotesoundlights.R;
import com.micdm.remotesoundlights.utils.ResourceRegistry;

import org.andengine.engine.Engine;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

public abstract class VisualizationSceneBuilder {

    public static interface OnSettingsListener {
        public void onSettings();
    }

    public static class Scene extends org.andengine.entity.scene.Scene {}

    private static final int SETTINGS_BUTTON_SIZE = 50;

    private static String getWaitingMessageText(Context context, SelectModeSceneBuilder.ModeType type) {
        if (type == SelectModeSceneBuilder.ModeType.GUEST) {
            return context.getString(R.string.wait_for_boss);
        }
        if (type == SelectModeSceneBuilder.ModeType.BOSS) {
            return context.getString(R.string.wait_for_music);
        }
        throw new RuntimeException(String.format("Unknown type %s", type));
    }

    private static void addWaitingMessage(Context context, Engine engine, Scene scene, SelectModeSceneBuilder.ModeType type) {
        Font font = ResourceRegistry.getFont();
        String text = getWaitingMessageText(context, type);
        Text label = new Text(0, 0, font, text, engine.getVertexBufferObjectManager());
        label.setHorizontalAlign(HorizontalAlign.CENTER);
        label.setLeading(-20);
        label.setX(engine.getCamera().getCenterX() - label.getWidth() / 2);
        label.setY(engine.getCamera().getCenterY() - label.getHeight() / 2);
        label.setColor(Color.WHITE);
        scene.attachChild(label);
    }

    private static void addSettingsButton(Engine engine, Scene scene, final OnSettingsListener listener) {
        TextureRegion region = TextureRegionFactory.extractFromTexture(ResourceRegistry.getSettingsTexture());
        ButtonSprite sprite = new ButtonSprite(engine.getCamera().getWidth() - SETTINGS_BUTTON_SIZE - 10, 10, region, engine.getVertexBufferObjectManager());
        sprite.setWidth(SETTINGS_BUTTON_SIZE);
        sprite.setHeight(SETTINGS_BUTTON_SIZE);
        sprite.setAlpha(0.3f);
        sprite.setOnClickListener(new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite sprite, float x, float y) {
                listener.onSettings();
            }
        });
        scene.registerTouchArea(sprite);
        scene.setTouchAreaBindingOnActionDownEnabled(true);
        scene.attachChild(sprite);
    }

    public static Scene build(Context context, Engine engine, SelectModeSceneBuilder.ModeType type, OnSettingsListener listener) {
        Scene scene = new Scene();
        scene.setBackground(new Background(Color.BLACK));
        addWaitingMessage(context, engine, scene, type);
        addSettingsButton(engine, scene, listener);
        return scene;
    }

    public static void prepare(Scene scene) {
        for (int i = scene.getChildCount() - 1; i >= 0; i -= 1) {
            IEntity entity = scene.getChildByIndex(i);
            if (entity instanceof Text) {
                scene.detachChild(entity);
            }
        }
    }
}
