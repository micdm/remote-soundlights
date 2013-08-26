package com.micdm.remotesoundlights.scenes;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.util.color.Color;

public abstract class BaseScene extends Scene {

    public BaseScene() {
        setBackground(new Background(Color.BLACK));
    }
}
