package com.micdm.remotesoundlights.data;

import com.micdm.remotesoundlights.activities.boss.Analyzer;

public class GainListPacket {

    private Analyzer.Gain[] gains;

    public GainListPacket(Analyzer.Gain[] gains) {
        this.gains = gains;
    }

    public Analyzer.Gain[] getGains() {
        return gains;
    }
}
