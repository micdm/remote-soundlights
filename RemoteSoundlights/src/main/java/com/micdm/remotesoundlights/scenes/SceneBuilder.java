package com.micdm.remotesoundlights.scenes;

import android.content.Context;

import org.andengine.engine.Engine;

public abstract class SceneBuilder {

    protected Context context;
    protected Engine engine;

    public SceneBuilder(Context context, Engine engine) {
        this.context = context;
        this.engine = engine;
    }
}
