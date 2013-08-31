package com.micdm.remotesoundlights.scenes;

import com.micdm.remotesoundlights.utils.Logger;
import com.micdm.remotesoundlights.visualizers.Visualizer;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.AverageFPSCounter;
import org.andengine.util.color.Color;

public class VisualizationSceneBuilder {

    public static class Scene extends org.andengine.entity.scene.Scene {}

    private Visualizer visualizer;

    public VisualizationSceneBuilder(Visualizer visualizer) {
        this.visualizer = visualizer;
    }

    private void addUpdateHandlers(Scene scene) {
        scene.registerUpdateHandler(visualizer.getSpriteHandler());
        scene.registerUpdateHandler(new AverageFPSCounter() {
            @Override
            protected void onHandleAverageDurationElapsed(float fps) {
                Logger.debug("FPS is %s now", fps);
            }
        });
    }

    public Scene build() {
        Scene scene = new Scene();
        scene.setBackground(new Background(Color.BLACK));
        addUpdateHandlers(scene);
        return scene;
    }
}
