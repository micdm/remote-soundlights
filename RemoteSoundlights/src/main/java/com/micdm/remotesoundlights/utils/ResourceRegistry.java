package com.micdm.remotesoundlights.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;

import org.andengine.engine.Engine;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.FontLibrary;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.util.adt.io.in.AssetInputStreamOpener;
import org.andengine.util.adt.map.Library;

import java.io.IOException;

public class ResourceRegistry {

    private static FontLibrary fonts = new FontLibrary();
    private static Library<BitmapTexture> textures = new Library<BitmapTexture>();

    private static Font loadFont(Context context, Engine engine, String name) {
        BitmapTextureAtlas atlas = new BitmapTextureAtlas(engine.getTextureManager(), 256, 128);
        String path = String.format("fonts/%s.ttf", name);
        Font font = FontFactory.createFromAsset(engine.getFontManager(), atlas, context.getAssets(), path, 48, true, Color.WHITE);
        engine.getTextureManager().loadTexture(atlas);
        engine.getFontManager().loadFont(font);
        return font;
    }

    private static String getTextureDirectory(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.densityDpi >= DisplayMetrics.DENSITY_HIGH ? "high" : "medium";
    }

    private static BitmapTexture loadTexture(Context context, Engine engine, String name) {
        try {
            String path = String.format("gfx/%s/%s.png", getTextureDirectory(context), name);
            AssetInputStreamOpener opener = new AssetInputStreamOpener(context.getAssets(), path);
            return (BitmapTexture) engine.getTextureManager().getTexture(name, opener, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR);
        } catch (IOException e) {
            throw new RuntimeException("Can not load texture");
        }
    }

    public static void loadMinimal(Context context, Engine engine) {
        fonts.clear();
        fonts.put(0, loadFont(context, engine, "PT_Sans-Narrow-Web-Bold"));
    }

    public static void load(Context context, Engine engine) {
        textures.clear();
        textures.put(0, loadTexture(context, engine, "star"));
        textures.put(1, loadTexture(context, engine, "settings"));
    }

    public static Font getFont() {
        return fonts.get(0);
    }

    public static BitmapTexture getStarTexture() {
        return textures.get(0);
    }

    public static BitmapTexture getSettingsTexture() {
        return textures.get(1);
    }
}
