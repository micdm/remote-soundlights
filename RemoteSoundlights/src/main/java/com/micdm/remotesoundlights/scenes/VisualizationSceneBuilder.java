package com.micdm.remotesoundlights.scenes;

import android.content.Context;

import com.micdm.remotesoundlights.R;
import com.micdm.remotesoundlights.utils.ResourceRegistry;
import com.micdm.remotesoundlights.visualizers.Visualizer;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

public class VisualizationSceneBuilder extends SceneBuilder {

    public static class Scene extends org.andengine.entity.scene.Scene {}

    public VisualizationSceneBuilder(Context context, Engine engine) {
        super(context, engine);
    }

    private String getWaitingMessageText(SelectModeSceneBuilder.ModeType type) {
        if (type == SelectModeSceneBuilder.ModeType.GUEST) {
            return context.getString(R.string.wait_for_boss);
        }
        if (type == SelectModeSceneBuilder.ModeType.BOSS) {
            return context.getString(R.string.wait_for_music);
        }
        throw new RuntimeException(String.format("Unknown type %s", type));
    }

    private void addWaitingMessage(Scene scene, SelectModeSceneBuilder.ModeType type) {
        Font font = ResourceRegistry.getFont();
        String text = getWaitingMessageText(type);
        Text label = new Text(0, 0, font, text, engine.getVertexBufferObjectManager());
        label.setHorizontalAlign(HorizontalAlign.CENTER);
        label.setLeading(-20);
        label.setX(engine.getCamera().getCenterX() - label.getWidth() / 2);
        label.setY(engine.getCamera().getCenterY() - label.getHeight() / 2);
        label.setColor(Color.WHITE);
        scene.attachChild(label);
    }

    private void addUpdateHandlers(Scene scene, Visualizer visualizer) {
        scene.registerUpdateHandler(visualizer.getSpriteHandler());
    }

    public Scene build(Visualizer visualizer, SelectModeSceneBuilder.ModeType type) {
        Scene scene = new Scene();
        scene.setBackground(new Background(Color.BLACK));
        addWaitingMessage(scene, type);
        addUpdateHandlers(scene, visualizer);
        return scene;
    }
}
