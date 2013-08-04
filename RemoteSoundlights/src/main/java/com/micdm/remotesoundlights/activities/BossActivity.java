package com.micdm.remotesoundlights.activities;

import android.media.audiofx.Visualizer;
import android.os.Bundle;

import com.micdm.remotesoundlights.R;

public class BossActivity extends PartyActivity {

    private Visualizer visualizer;

    private void setupVisualizer() {
        visualizer = new Visualizer(0);
        int[] range = Visualizer.getCaptureSizeRange();
        visualizer.setCaptureSize(range[1]);
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] data, int rate) {}
            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] data, int rate) {
                TouchView view = (TouchView) findViewById(R.id.touch);
                view.setFftData(data);
            }
        }, 10000, false, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.party);
        startReceiverAndSender();
        setupVisualizer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        visualizer.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        visualizer.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        visualizer.release();
        stopReceiverAndSender();
    }
}
