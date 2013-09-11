package com.micdm.remotesoundlights.modes.boss;

import java.util.concurrent.ArrayBlockingQueue;

public class AnalyzerThread extends Thread {

    public static interface OnPeakListener {
        public void onPeak(Analyzer.Peak[] peaks);
    }

    private static final int QUEUE_SIZE = 100;

    private boolean isActive = true;
    private ArrayBlockingQueue<byte[]> queue = new ArrayBlockingQueue<byte[]>(QUEUE_SIZE);
    private Analyzer analyzer = new Analyzer(new Analyzer.OnPeakListener() {
        @Override
        public void onPeak(Analyzer.Peak[] peaks) {
            listener.onPeak(peaks);
        }
    });
    private OnPeakListener listener;

    public AnalyzerThread(OnPeakListener listener) {
        this.listener = listener;
    }

    public void setFftData(byte[] data) {
        queue.offer(data);
    }

    @Override
    public void run() {
        while (isActive) {
            byte[] data = queue.poll();
            if (data != null) {
                analyzer.setFftData(data);
            } else {
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public void cancel() {
        isActive = false;
    }
}
