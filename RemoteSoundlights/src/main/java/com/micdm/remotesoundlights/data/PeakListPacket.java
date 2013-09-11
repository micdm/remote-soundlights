package com.micdm.remotesoundlights.data;

import com.micdm.remotesoundlights.modes.boss.Analyzer;

public class PeakListPacket {

    private Analyzer.Peak[] peaks;

    public PeakListPacket(Analyzer.Peak[] peaks) {
        this.peaks = peaks;
    }

    public Analyzer.Peak[] getPeaks() {
        return peaks;
    }
}
