package com.micdm.remotesoundlights.activities.boss;

import java.util.ArrayList;

public class Analyzer {

    public static interface OnGainListener {
        public void onGain(Gain[] gains);
    }

    public static class Gain {

        private LEVEL level;
        private float value;

        public Gain(LEVEL level, float value) {
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

    public static enum TYPE {
        NORMAL,
        AMPLIFIED
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
    }

    private static final int[] FREQUENCIES = {10, 80, 200, 500, 2500, 500, 10000, 20000};

    private int[] prev;
    private TYPE type;
    private OnGainListener listener;

    public Analyzer(TYPE type, OnGainListener listener) {
        this.type = type;
        this.listener = listener;
    }

    private int getFrequencyLevel(byte real, byte imaginary) {
        if (real == 0 && imaginary == 0) {
            return 0;
        }
        double magnitude = Math.sqrt(Math.pow(real, 2) + Math.pow(imaginary, 2));
        if (type == TYPE.NORMAL) {
            return (int) Math.min(magnitude, 0xFF);
        }
        if (type == TYPE.AMPLIFIED) {
            return Math.min((int) Math.floor(100 * Math.log10(magnitude)), 0xFF);
        }
        return 0;
    }

    private int[] getFrequencyLevels(byte[] data) {
        int[] levels = new int[data.length / 2];
        levels[0] = getFrequencyLevel(data[0], (byte) 0);
        for (int i = 1; i < levels.length - 1; i += 1) {
            levels[i] = getFrequencyLevel(data[i * 2], data[i * 2 + 1]);
        }
        levels[levels.length - 1] = getFrequencyLevel(data[1], (byte) 0);
        return levels;
    }

    private int getMaxLevel(int[] levels, int minFrequency, int maxFrequency) {
        double step = (float) FREQUENCIES[FREQUENCIES.length - 1] / levels.length;
        int begin = (int) (minFrequency / step);
        int end = (int) (maxFrequency / step);
        int max = 0;
        for (int i = begin; i < end; i += 1) {
            max = Math.max(max, levels[i]);
        }
        return max;
    }

    private int[] getCompressedFrequencyLevels(int[] levels) {
        int[] compressed = new int[FREQUENCIES.length - 1];
        for (int i = 0; i < FREQUENCIES.length - 1; i += 1) {
            compressed[i] = getMaxLevel(levels, FREQUENCIES[i], FREQUENCIES[i + 1]);
        }
        return compressed;
    }

    private boolean isGain(int[] compressed, int index) {
        if (prev == null || prev[index] == 0) {
            return compressed[index] != 0;
        }
        return (float) compressed[index] / prev[index] > 4;
    }

    private Gain[] getGains(int[] compressed) {
        ArrayList<Gain> gains = new ArrayList<Gain>();
        LEVEL[] levels = LEVEL.values();
        for (int i = 0; i < levels.length; i += 1) {
            if (isGain(compressed, i)) {
                gains.add(new Gain(levels[i], compressed[i]));
            }
        }
        Gain[] content = new Gain[gains.size()];
        return gains.toArray(content);
    }

    public void setFftData(byte[] data) {
        int[] levels = getFrequencyLevels(data);
        int[] compressed = getCompressedFrequencyLevels(levels);
        Gain[] gains = getGains(compressed);
        if (gains.length != 0) {
            listener.onGain(gains);
        }
        prev = compressed;
    }
}
