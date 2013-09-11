package com.micdm.remotesoundlights.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;

import org.andengine.engine.Engine;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.util.adt.io.in.AssetInputStreamOpener;

import java.io.IOException;

public class ResourceRegistry {

    private static Font font;
    private static BitmapTexture texture;

    private static void loadFont(Context context, Engine engine) {
        BitmapTextureAtlas atlas = new BitmapTextureAtlas(engine.getTextureManager(), 512, 512);
        Font font = FontFactory.createFromAsset(engine.getFontManager(), atlas, context.getAssets(), "fonts/OpenSans-CondBold.ttf", 48, true, Color.WHITE);
        engine.getTextureManager().loadTexture(atlas);
        engine.getFontManager().loadFont(font);
        ResourceRegistry.font = font;
    }

    private static String getTextureName(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        if (metrics.densityDpi >= DisplayMetrics.DENSITY_XHIGH) {
            return "xhigh";
        }
        if (metrics.densityDpi >= DisplayMetrics.DENSITY_HIGH) {
            return "high";
        }
        return "medium";
    }

    private static void loadTexture(Context context, Engine engine) {
        try {
            String path = String.format("gfx/%s.png", getTextureName(context));
            AssetInputStreamOpener opener = new AssetInputStreamOpener(context.getAssets(), path);
            ResourceRegistry.texture = (BitmapTexture) engine.getTextureManager().getTexture("point", opener, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR);
        } catch (IOException e) {
            throw new RuntimeException("Can not load texture");
        }
    }

    public static void load(Context context, Engine engine) {
        loadFont(context, engine);
        loadTexture(context, engine);
    }

    public static Font getFont() {
        return font;
    }

    public static BitmapTexture getTexture() {
        return texture;
    }
}
