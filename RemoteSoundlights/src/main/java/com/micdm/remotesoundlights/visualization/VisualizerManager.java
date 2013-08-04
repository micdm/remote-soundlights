package com.micdm.remotesoundlights.visualization;

public class VisualizerManager {

    private static final int MIN_FREQUENCY = 10;
    private static final int LOW_BASS_FREQUENCY = 80;
    private static final int HIGH_BASS_FREQUENCY = 200;
    private static final int LOW_MIDDLE_FREQUENCY = 500;
    private static final int MEDIUM_MIDDLE_FREQUENCY = 2500;
    private static final int HIGH_MIDDLE_FREQUENCY = 5000;
    private static final int LOW_HIGH_FREQUENCY = 10000;
    private static final int HIGH_HIGH_FREQUENCY = 20000;

    private static int getFrequencyLevel(byte real, byte imaginary) {
        if (real == 0 && imaginary == 0) {
            return 0;
        }
        double magnitude = Math.sqrt(Math.pow(real, 2) + Math.pow(imaginary, 2));
        return Math.min((int) Math.floor(100 * Math.log10(magnitude)), 0xFF);
    }

    private static int[] getFrequencyLevels(byte[] data) {
        int[] levels = new int[data.length / 2];
        levels[0] = getFrequencyLevel(data[0], (byte) 0);
        for (int i = 1; i < levels.length - 1; i += 1) {
            levels[i] = getFrequencyLevel(data[i * 2], data[i * 2 + 1]);
        }
        levels[levels.length - 1] = getFrequencyLevel(data[1], (byte) 0);
        return levels;
    }

    private static int getAverageLevel(int[] levels, int minFrequency, int maxFrequency) {
        double step = (float) HIGH_HIGH_FREQUENCY / levels.length;
        int begin = (int) (minFrequency / step);
        int end = (int) (maxFrequency / step);
        int average = 0;
        for (int i = begin; i < end; i += 1) {
            average += levels[i];
        }
        average /= (end - begin);
        return average;
    }

    private static int[] getCompressedFrequencyLevels(int[] levels) {
        int[] compressed = new int[7];
        compressed[0] = getAverageLevel(levels, MIN_FREQUENCY, LOW_BASS_FREQUENCY);
        compressed[1] = getAverageLevel(levels, LOW_BASS_FREQUENCY, HIGH_BASS_FREQUENCY);
        compressed[2] = getAverageLevel(levels, HIGH_BASS_FREQUENCY, LOW_MIDDLE_FREQUENCY);
        compressed[3] = getAverageLevel(levels, LOW_MIDDLE_FREQUENCY, MEDIUM_MIDDLE_FREQUENCY);
        compressed[4] = getAverageLevel(levels, MEDIUM_MIDDLE_FREQUENCY, HIGH_MIDDLE_FREQUENCY);
        compressed[5] = getAverageLevel(levels, HIGH_MIDDLE_FREQUENCY, LOW_HIGH_FREQUENCY);
        compressed[6] = getAverageLevel(levels, LOW_HIGH_FREQUENCY, HIGH_HIGH_FREQUENCY);
        return compressed;
    }

    private static boolean isGain(int[] compressed, int[] prev, int index) {
        if (prev[index] == 0) {
            return compressed[index] != 0;
        }
        return (float) compressed[index] / prev[index] > 2;
    }

    public static void setFftData(Visualizer visualizer, byte[] data) {
        int[] levels = getFrequencyLevels(data);
        int[] compressed = getCompressedFrequencyLevels(levels);
        int[] prev = visualizer.getLevels();
        visualizer.setLevels(compressed);
        if (prev == null) {
            return;
        }
        if (isGain(compressed, prev, 0)) {
            visualizer.onLowBass();
        }
        if (isGain(compressed, prev, 1)) {
            visualizer.onHighBass();
        }
        if (isGain(compressed, prev, 2)) {
            visualizer.onLowMiddle();
        }
        if (isGain(compressed, prev, 3)) {
            visualizer.onMediumMiddle();
        }
        if (isGain(compressed, prev, 4)) {
            visualizer.onHighMiddle();
        }
        if (isGain(compressed, prev, 5)) {
            visualizer.onLowHigh();
        }
        if (isGain(compressed, prev, 6)) {
            visualizer.onHighHigh();
        }
    }
}
