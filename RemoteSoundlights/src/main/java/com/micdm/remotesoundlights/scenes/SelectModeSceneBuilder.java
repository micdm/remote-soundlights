package com.micdm.remotesoundlights.scenes;

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

public class SelectModeSceneBuilder extends SceneBuilder {

    public static interface OnSelectModeListener {
        public void onSelectMode(ModeType type);
    }

    public static class Scene extends org.andengine.entity.scene.Scene {}

    public static enum ModeType {
        GUEST,
        BOSS
    }

    private static final int BUTTON_SIZE = 400;

    public SelectModeSceneBuilder(Context context, Engine engine) {
        super(context, engine);
    }

    private ButtonSprite addButton(Scene scene, float x, float y, Color color, String text) {
        TextureRegion region = TextureRegionFactory.extractFromTexture(ResourceRegistry.getTexture());
        ButtonSprite sprite = new ButtonSprite(x, y, region, engine.getVertexBufferObjectManager());
        sprite.setWidth(BUTTON_SIZE);
        sprite.setHeight(BUTTON_SIZE);
        sprite.setColor(color);
        addButtonLabel(sprite, text);
        scene.registerTouchArea(sprite);
        scene.setTouchAreaBindingOnActionDownEnabled(true);
        scene.attachChild(sprite);
        return sprite;
    }

    private void addButtonLabel(Sprite sprite, String text) {
        Font font = ResourceRegistry.getFont();
        Text label = new Text(0, 0, font, text, engine.getVertexBufferObjectManager());
        label.setHorizontalAlign(HorizontalAlign.CENTER);
        label.setLeading(-20);
        label.setX(BUTTON_SIZE / 2 - label.getWidth() / 2);
        label.setY(BUTTON_SIZE / 2 - label.getHeight() / 2 + 7);
        label.setColor(Color.BLACK);
        sprite.attachChild(label);
    }

    private void addBossButton(Scene scene, final OnSelectModeListener listener) {
        float x = engine.getCamera().getCenterX() - BUTTON_SIZE;
        float y = engine.getCamera().getCenterY() - BUTTON_SIZE / 2;
        Color color = ColorUtils.convertARGBPackedIntToColor(context.getResources().getColor(R.color.boss_button));
        ButtonSprite sprite = addButton(scene, x, y, color, context.getString(R.string.select_mode_boss));
        sprite.setOnClickListener(new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite sprite, float x, float y) {
                listener.onSelectMode(ModeType.BOSS);
            }
        });
    }

    private void addGuestButton(Scene scene, final OnSelectModeListener listener) {
        float x = engine.getCamera().getCenterX();
        float y = engine.getCamera().getCenterY() - BUTTON_SIZE / 2;
        Color color = ColorUtils.convertARGBPackedIntToColor(context.getResources().getColor(R.color.guest_button));
        ButtonSprite sprite = addButton(scene, x, y, color, context.getString(R.string.select_mode_guest));
        sprite.setOnClickListener(new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite sprite, float x, float y) {
                listener.onSelectMode(ModeType.GUEST);
            }
        });
    }

    public Scene build(OnSelectModeListener listener) {
        Scene scene = new Scene();
        scene.setBackground(new Background(Color.BLACK));
        addBossButton(scene, listener);
        addGuestButton(scene, listener);
        return scene;
    }
}
