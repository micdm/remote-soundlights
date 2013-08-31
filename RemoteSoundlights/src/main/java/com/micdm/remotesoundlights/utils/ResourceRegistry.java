package com.micdm.remotesoundlights.utils;

import android.content.Context;

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
        BitmapTextureAtlas atlas = new BitmapTextureAtlas(engine.getTextureManager(), 512, 64);
        Font font = FontFactory.createFromAsset(engine.getFontManager(), atlas, context.getAssets(), "fonts/BebasNeue.otf", 60, true, 0xFFFFFFFF);
        engine.getTextureManager().loadTexture(atlas);
        engine.getFontManager().loadFont(font);
        ResourceRegistry.font = font;
    }

    private static void loadTexture(Context context, Engine engine) {
        try {
            AssetInputStreamOpener opener = new AssetInputStreamOpener(context.getAssets(), "gfx/star.png");
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
