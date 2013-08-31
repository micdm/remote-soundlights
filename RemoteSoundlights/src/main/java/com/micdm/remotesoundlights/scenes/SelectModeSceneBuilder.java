package com.micdm.remotesoundlights.scenes;

import android.content.Context;

import com.micdm.remotesoundlights.R;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.adt.io.in.AssetInputStreamOpener;
import org.andengine.util.color.Color;
import org.andengine.util.color.ColorUtils;

import java.io.IOException;

public class SelectModeSceneBuilder {

    public static interface OnSelectModeListener {
        public void onSelectMode(ModeType type);
    }

    public static class Scene extends org.andengine.entity.scene.Scene {}

    public static enum ModeType {
        GUEST,
        BOSS
    }

    private Context context;
    private Engine engine;
    private OnSelectModeListener listener;

    public SelectModeSceneBuilder(Context context, Engine engine, OnSelectModeListener listener) {
        this.context = context;
        this.engine = engine;
        this.listener = listener;
    }

    private TextureRegion getTextureRegion() {
        try {
            TextureManager manager = engine.getTextureManager();
            AssetInputStreamOpener opener = new AssetInputStreamOpener(context.getAssets(), "gfx/star.png");
            BitmapTexture texture = (BitmapTexture) manager.getTexture("point", opener, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR);
            return TextureRegionFactory.extractFromTexture(texture);
        } catch (IOException e) {
            throw new RuntimeException("Can not load texture");
        }
    }

    private Font getFont() {
        BitmapTextureAtlas atlas = new BitmapTextureAtlas(engine.getTextureManager(), 512, 64);
        Font font = FontFactory.createFromAsset(engine.getFontManager(), atlas, context.getAssets(), "fonts/BebasNeue.otf", 60, true, 0xFFFFFFFF);
        engine.getTextureManager().loadTexture(atlas);
        engine.getFontManager().loadFont(font);
        return font;
    }

    private void addButton(Scene scene, float x, float y, Color color, String text, final ModeType type) {
        TextureRegion region = getTextureRegion();
        Sprite sprite = new ButtonSprite(x, y, region, engine.getVertexBufferObjectManager(), new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite sprite, float x, float y) {
                listener.onSelectMode(type);
            }
        });
        sprite.setWidth(400);
        sprite.setHeight(400);
        sprite.setColor(color);
        addButtonLabel(sprite, text);
        scene.registerTouchArea(sprite);
        scene.setTouchAreaBindingOnActionDownEnabled(true);
        scene.attachChild(sprite);
    }

    private void addButtonLabel(Sprite sprite, String text) {
        Font font = getFont();
        Text label = new Text(0, 0, font, text, engine.getVertexBufferObjectManager());
        label.setHorizontalAlign(HorizontalAlign.CENTER);
        label.setLeading(-20);
        label.setX(200 - label.getWidth() / 2);
        label.setY(205 - label.getHeight() / 2 + 10);
        label.setColor(Color.BLACK);
        sprite.attachChild(label);
    }

    private void addBossButton(Scene scene) {
        float x = engine.getCamera().getCenterX() - 400;
        float y = engine.getCamera().getCenterY() - 200;
        Color color = ColorUtils.convertARGBPackedIntToColor(0xFFFF5959);
        addButton(scene, x, y, color, context.getString(R.string.select_mode_boss), ModeType.BOSS);
    }

    private void addGuestButton(Scene scene) {
        float x = engine.getCamera().getCenterX();
        float y = engine.getCamera().getCenterY() - 200;
        Color color = ColorUtils.convertARGBPackedIntToColor(0xFF59ABFF);
        addButton(scene, x, y, color, context.getString(R.string.select_mode_guest), ModeType.GUEST);
    }

    public Scene build() {
        Scene scene = new Scene();
        scene.setBackground(new Background(Color.BLACK));
        addBossButton(scene);
        addGuestButton(scene);
        return scene;
    }
}
