package com.micdm.remotesoundlights.activities;

import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.view.View;

import com.micdm.remotesoundlights.R;
import com.micdm.remotesoundlights.data.DataPacket;

import java.nio.ByteBuffer;

public class BossActivity extends PartyActivity {

    private Visualizer visualizer;

    private void setupTouchArea() {
        TouchView view = (TouchView) findViewById(R.id.touch);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int color = 0xFF000000 + (int) (Math.random() * 0xFFFFFF);
                ((TouchView) view).setColor(color);
                sendData(color);
            }
        });
    }

    private short[] getFrequencyValues(byte[] data) {
        short[] values = new short[data.length / 2];
        values[0] = data[0];
        for (int i = 1; i < values.length - 1; i += 1) {
            double magnitude = Math.sqrt(Math.pow(data[i * 2], 2) + Math.pow(data[i * 2 + 1], 2));
            //values[i] = (short) Math.floor(10 * Math.log10(magnitude));
            values[i] = (short) Math.floor(magnitude);
        }
        values[values.length - 1] = data[1];
        return values;
    }

    private short[] getCompressedFrequencyValues(short[] data) {
        short[] values = new short[7];
        int end = data.length / 4;
        for (int i = 0; i < values.length; i += 1) {
            //int begin = (i == values.length - 1) ? 0 : end / 2;
            int begin = end / 2;
            int avg = 0;
            for (int j = begin; j < end; j += 1) {
                avg += data[j];
            }
            avg /= (end - begin);
            values[values.length - 1 - i] = (short) avg;
            end = begin;
        }
        return values;
    }

    private void setupVisualizer() {
        visualizer = new Visualizer(0);
        int[] range = Visualizer.getCaptureSizeRange();
        visualizer.setCaptureSize(range[1]);
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] data, int rate) {}
            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] data, int rate) {
                short[] values = getFrequencyValues(data);
                short[] compressed = getCompressedFrequencyValues(values);
                TouchView view = (TouchView) findViewById(R.id.touch);
                view.setValues(compressed);
            }
        }, 20000, false, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.party);
        startReceiverAndSender();
        setupVisualizer();
        //setupTouchArea();
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

    @Override
    protected void onDataReceived(DataPacket packet) {
        TouchView view = (TouchView) findViewById(R.id.touch);
        view.setColor((int) packet.getContent());
    }
}
