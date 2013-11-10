package com.micdm.remotesoundlights.scene_builders;

import android.content.Context;

import com.micdm.remotesoundlights.R;
import com.micdm.remotesoundlights.utils.ResourceRegistry;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

public abstract class LoadingSceneBuilder {

    public static class Scene extends org.andengine.entity.scene.Scene {}

    private static void addLoadingMessage(Context context, Engine engine, Scene scene) {
        Font font = ResourceRegistry.getFont();
        Text label = new Text(0, 0, font, context.getString(R.string.loading), engine.getVertexBufferObjectManager());
        label.setHorizontalAlign(HorizontalAlign.CENTER);
        label.setX(engine.getCamera().getCenterX() - label.getWidth() / 2);
        label.setY(engine.getCamera().getCenterY() - label.getHeight() / 2);
        label.setColor(Color.WHITE);
        scene.attachChild(label);
    }

    public static Scene build(Context context, Engine engine) {
        Scene scene = new Scene();
        scene.setBackground(new Background(Color.BLACK));
        addLoadingMessage(context, engine, scene);
        return scene;
    }
}
