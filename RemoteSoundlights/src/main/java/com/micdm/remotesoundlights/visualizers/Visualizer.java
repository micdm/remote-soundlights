package com.micdm.remotesoundlights.visualizers;

import com.micdm.remotesoundlights.modes.boss.Analyzer;

public interface Visualizer {
    public void start();
    public void stop();
    public void visualize(Analyzer.Peak[] peaks);
}
