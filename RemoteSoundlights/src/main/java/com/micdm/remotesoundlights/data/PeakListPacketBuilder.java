package com.micdm.remotesoundlights.data;

import com.micdm.remotesoundlights.modes.boss.Analyzer;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class PeakListPacketBuilder {

    private static final int MAX_PACKET_SIZE = 256;

    private static Analyzer.LEVEL getLevel(int number) {
        for (Analyzer.LEVEL level: Analyzer.LEVEL.values()) {
            if (level.getNumber() == number) {
                return level;
            }
        }
        return null;
    }

    public static byte[] encode(PeakListPacket packet) {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
        Analyzer.Peak[] peaks = packet.getPeaks();
        buffer.putInt(peaks.length);
        for (Analyzer.Peak peak : peaks) {
            buffer.putInt(peak.getLevel().getNumber());
            buffer.putFloat(peak.getValue());
        }
        return buffer.array();
    }

    public static PeakListPacket decode(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int count = buffer.getInt();
        ArrayList<Analyzer.Peak> peaks = new ArrayList<Analyzer.Peak>(count);
        for (int i = 0; i < count; i += 1) {
            Analyzer.LEVEL level = getLevel(buffer.getInt());
            float value = buffer.getFloat();
            peaks.add(new Analyzer.Peak(level, value));
        }
        Analyzer.Peak[] content = new Analyzer.Peak[count];
        return new PeakListPacket(peaks.toArray(content));
    }
}
