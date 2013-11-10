package com.micdm.remotesoundlights.modes.boss;

import java.util.ArrayList;
import java.util.LinkedList;

public class Analyzer {

    public static interface OnPeakListener {
        public void onPeak(Peak[] peaks);
    }

    public static class Peak {

        private LEVEL level;
        private float value;

        public Peak(LEVEL level, float value) {
            this.level = level;
            this.value = value;
        }

        public LEVEL getLevel() {
            return level;
        }

        public float getValue() {
            return value;
        }
    }

    public static enum LEVEL {
        LOW_BASS(0),
        HIGH_BASS(1),
        LOW_MIDDLE(2),
        MEDIUM_MIDDLE(3),
        HIGH_MIDDLE(4),
        LOW_HIGH(5),
        HIGH_HIGH(6);

        private int number;

        private LEVEL(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public boolean isBass() {
            return this == LOW_BASS || this == HIGH_BASS;
        }

        public boolean isMiddle() {
            return this == LOW_MIDDLE || this == MEDIUM_MIDDLE || this == HIGH_MIDDLE;
        }

        public boolean isHigh() {
            return this == LOW_HIGH || this == HIGH_HIGH;
        }
    }

    private static class BeatDetector {

        private static final int MAX_HISTORY_SIZE = 200;
        private static final double MAGICK = 1.3;
        private static final int DETECTION_THRESHOLD = 5;

        private LinkedList<Double> history = new LinkedList<Double>();
        private double sum;
        private int detections;

        public boolean addEnergy(double energy) {
            history.add(energy);
            sum += energy;
            if (history.size() > MAX_HISTORY_SIZE) {
                sum -= history.poll();
            }
            double average = sum / history.size();
            if (energy > average * MAGICK) {
                detections += 1;
            }
            if (detections == DETECTION_THRESHOLD) {
                detections = 0;
                return true;
            }
            return false;
        }
    }

    private static final int[] FREQUENCIES = {10, 80, 200, 500, 2500, 500, 10000, 20000};

    private BeatDetector[] detectors = new BeatDetector[LEVEL.values().length];
    private OnPeakListener listener;

    public Analyzer(OnPeakListener listener) {
        initDetectors();
        this.listener = listener;
    }

    private void initDetectors() {
        for (int i = 0; i < detectors.length; i += 1) {
            detectors[i] = new BeatDetector();
        }
    }

    private double getFrequencyLevel(byte real, byte imaginary) {
        return Math.sqrt(real * real + imaginary * imaginary);
    }

    private double[] getFrequencyLevels(byte[] data) {
        double[] levels = new double[data.length / 2];
        levels[0] = getFrequencyLevel(data[0], (byte) 0);
        for (int i = 1; i < levels.length - 1; i += 1) {
            levels[i] = getFrequencyLevel(data[i * 2], data[i * 2 + 1]);
        }
        levels[levels.length - 1] = getFrequencyLevel(data[1], (byte) 0);
        return levels;
    }

    private double getEnergy(double[] levels, int minFrequency, int maxFrequency) {
        double step = FREQUENCIES[FREQUENCIES.length - 1] / levels.length;
        int begin = (int) (minFrequency / step);
        int end = (int) (maxFrequency / step);
        double energy = 0;
        for (int i = begin; i < end; i += 1) {
            if (levels[i] < 1) {
                continue;
            }
            energy += Math.log(levels[i]);
        }
        return energy;
    }

    private double[] getEnergies(double[] levels) {
        double[] energies = new double[FREQUENCIES.length - 1];
        for (int i = 0; i < FREQUENCIES.length - 1; i += 1) {
            energies[i] = getEnergy(levels, FREQUENCIES[i], FREQUENCIES[i + 1]);
        }
        return energies;
    }

    private Peak[] getPeaks(double[] energies) {
        ArrayList<Peak> peaks = new ArrayList<Peak>();
        LEVEL[] levels = LEVEL.values();
        for (int i = 0; i < levels.length; i += 1) {
            if (detectors[i].addEnergy(energies[i])) {
                peaks.add(new Peak(levels[i], (int) energies[i]));
            }
        }
        Peak[] content = new Peak[peaks.size()];
        return peaks.toArray(content);
    }

    public void setFftData(byte[] data) {
        double[] levels = getFrequencyLevels(data);
        double[] energies = getEnergies(levels);
        Peak[] peaks = getPeaks(energies);
        if (peaks.length != 0) {
            listener.onPeak(peaks);
        }
    }
}
