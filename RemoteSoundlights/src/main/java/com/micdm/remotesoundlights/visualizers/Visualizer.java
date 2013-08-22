package com.micdm.remotesoundlights.visualizers;

import com.micdm.remotesoundlights.activities.boss.Analyzer;

import org.andengine.engine.handler.IUpdateHandler;

public interface Visualizer {
    public IUpdateHandler getSpriteHandler();
    public void visualize(Analyzer.Gain[] gains);
}
