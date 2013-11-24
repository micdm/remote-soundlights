package com.micdm.remotesoundlights.modes.boss;

import android.media.audiofx.Visualizer;

class VisualizerWatcher {

    public static interface OnDataListener {
        public void onData(byte[] data);
    }

    private static final int RATE = 20000;

    private OnDataListener listener;
    private Visualizer visualizer;

    public VisualizerWatcher(OnDataListener listener) {
        this.listener = listener;
    }

    public void init() {
        visualizer = new Visualizer(0);
        visualizer.setEnabled(false);
        int[] range = Visualizer.getCaptureSizeRange();
        visualizer.setCaptureSize(range[1]);
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] data, int rate) {}
            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] data, int rate) {
                listener.onData(data.clone());
            }
        }, RATE, false, true);
        visualizer.setEnabled(true);
    }

    public void deinit() {
        visualizer.setEnabled(false);
        visualizer.release();
    }
}
