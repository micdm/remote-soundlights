package com.micdm.remotesoundlights.scene_builders;

import android.content.Context;

import com.micdm.remotesoundlights.R;
import com.micdm.remotesoundlights.utils.ResourceRegistry;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.color.ColorUtils;

public abstract class SelectModeSceneBuilder {

    public static interface OnSelectModeListener {
        public void onSelectMode(ModeType type);
    }

    public static class Scene extends org.andengine.entity.scene.Scene {}

    public static enum ModeType {
        GUEST,
        BOSS
    }

    private static final int MODE_BUTTON_SIZE = 400;

    private static ButtonSprite addModeButton(Engine engine, Scene scene, float x, float y, Color color, String text) {
        TextureRegion region = TextureRegionFactory.extractFromTexture(ResourceRegistry.getStarTexture());
        ButtonSprite sprite = new ButtonSprite(x, y, region, engine.getVertexBufferObjectManager());
        sprite.setWidth(MODE_BUTTON_SIZE);
        sprite.setHeight(MODE_BUTTON_SIZE);
        sprite.setColor(color);
        addModeButtonLabel(engine, sprite, text);
        scene.registerTouchArea(sprite);
        scene.setTouchAreaBindingOnActionDownEnabled(true);
        scene.attachChild(sprite);
        return sprite;
    }

    private static void addModeButtonLabel(Engine engine, Sprite sprite, String text) {
        Font font = ResourceRegistry.getFont();
        Text label = new Text(0, 0, font, text, engine.getVertexBufferObjectManager());
        label.setHorizontalAlign(HorizontalAlign.CENTER);
        label.setLeading(-20);
        label.setX(MODE_BUTTON_SIZE / 2 - label.getWidth() / 2);
        label.setY(MODE_BUTTON_SIZE / 2 - label.getHeight() / 2 + 7);
        label.setColor(Color.BLACK);
        sprite.attachChild(label);
    }

    private static void addBossButton(Context context, Engine engine, Scene scene, final OnSelectModeListener listener) {
        float x = engine.getCamera().getCenterX() - MODE_BUTTON_SIZE;
        float y = engine.getCamera().getCenterY() - MODE_BUTTON_SIZE / 2;
        Color color = ColorUtils.convertARGBPackedIntToColor(context.getResources().getColor(R.color.boss_button));
        ButtonSprite sprite = addModeButton(engine, scene, x, y, color, context.getString(R.string.select_mode_boss));
        sprite.setOnClickListener(new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite sprite, float x, float y) {
                listener.onSelectMode(ModeType.BOSS);
            }
        });
    }

    private static void addGuestButton(Context context, Engine engine, Scene scene, final OnSelectModeListener listener) {
        float x = engine.getCamera().getCenterX();
        float y = engine.getCamera().getCenterY() - MODE_BUTTON_SIZE / 2;
        Color color = ColorUtils.convertARGBPackedIntToColor(context.getResources().getColor(R.color.guest_button));
        ButtonSprite sprite = addModeButton(engine, scene, x, y, color, context.getString(R.string.select_mode_guest));
        sprite.setOnClickListener(new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite sprite, float x, float y) {
                listener.onSelectMode(ModeType.GUEST);
            }
        });
    }

    public static Scene build(Context context, Engine engine, OnSelectModeListener listener) {
        Scene scene = new Scene();
        scene.setBackground(new Background(Color.BLACK));
        addBossButton(context, engine, scene, listener);
        addGuestButton(context, engine, scene, listener);
        return scene;
    }
}
