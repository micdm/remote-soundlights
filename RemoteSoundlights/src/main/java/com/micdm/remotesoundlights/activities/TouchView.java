package com.micdm.remotesoundlights.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TouchView extends View {

    private int color = android.R.color.black;

    public TouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawColor(color);
        if (values == null) {
            return;
        }
        canvas.drawColor(0);
        Paint paint = new Paint();
        paint.setColor(0xFFFFFF00);
        for (int i = 0; i < values.length; i += 1) {
            int value = values[i];
            float width = canvas.getWidth() / values.length;
            float height = value * canvas.getHeight() / 255f;
            canvas.drawRect(i * width, 0, (i + 1) * width, height, paint);
        }
    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

    private short[] values;
    public void setValues(short[] values) {
        this.values = values;
        invalidate();
    }
}
