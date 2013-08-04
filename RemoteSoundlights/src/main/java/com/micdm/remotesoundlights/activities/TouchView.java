package com.micdm.remotesoundlights.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.micdm.remotesoundlights.visualization.Visualizer;
import com.micdm.remotesoundlights.visualization.VisualizerManager;

public class TouchView extends View {

    private Visualizer visualizer = new Visualizer();

    public TouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(android.R.color.black);
        visualizer.draw(canvas);
        invalidate();
    }

    public void setFftData(byte[] data) {
        VisualizerManager.setFftData(visualizer, data);
    }
}
