package com.micdm.remotesoundlights.visualization;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Visualizer {

    private class Circle {

        private int color;
        private int x;
        private int y;
        private int startRadius;
        private int radius;

        public Circle(int color, int x, int y, int radius) {
            this.color = color;
            this.x = x;
            this.y = y;
            this.startRadius = radius;
            this.radius = radius;
        }

        public int getColor() {
            return color;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getRadius() {
            return radius;
        }

        public boolean onTick() {
            radius += 20;
            color -= 0x0F000000;
            return (radius - startRadius) < 320;
        }
    }

    private int[] levels;

    public int[] getLevels() {
        return levels;
    }

    public void setLevels(int[] levels) {
        this.levels = levels;
    }

    private ArrayList<Circle> circles = new ArrayList<Circle>();

    private void addCircle(int color, int size) {
        int x = (int) (Math.random() * 2000);
        int y = (int) (Math.random() * 1000);
        circles.add(new Circle(0xFF000000 + color, x, y, size * 2));
        Collections.sort(circles, new Comparator<Circle>() {
            @Override
            public int compare(Circle a, Circle b) {
                return a.getRadius() > b.getRadius() ? 1 : -1;
            }
        });
    }

    public void onLowBass() {
        addCircle(0xFF0000, 500);
    }

    public void onHighBass() {
        addCircle(0x00FF00, 500);
    }

    public void onLowMiddle() {
        addCircle(0x0000FF, 100);
    }

    public void onMediumMiddle() {
        addCircle(0xFFFF00, 100);
    }

    public void onHighMiddle() {
        addCircle(0xFF00FF, 100);
    }

    public void onLowHigh() {
        addCircle(0x00FFFF, 10);
    }

    public void onHighHigh() {
        addCircle(0xFFFFFFFF, 10);
    }

    private void drawCircles(Canvas canvas) {
        Paint paint = new Paint();
        for (int i = circles.size() - 1; i >= 0; i -= 1) {
            Circle circle = circles.get(i);
            paint.setColor(circle.getColor());
            canvas.drawCircle(circle.getX(), circle.getY(), circle.getRadius(), paint);
            if (!circle.onTick()) {
                circles.remove(circle);
            }
        }
    }

    private void drawBands(Canvas canvas) {
        if (levels == null) {
            return;
        }
        Paint paint = new Paint();
        paint.setColor(0x33FFFF00);
        int width = canvas.getWidth() / levels.length;
        for (int i = 0; i < levels.length; i += 1) {
            int level = levels[i];
            int height = (int) (canvas.getHeight() * (level / 255f));
            canvas.drawRect(i * width, 0, (i + 1) * width, height, paint);
        }
    }

    public void draw(Canvas canvas) {
        drawCircles(canvas);
        drawBands(canvas);
    }
}
