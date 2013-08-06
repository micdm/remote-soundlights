package com.micdm.remotesoundlights.data;

import com.micdm.remotesoundlights.activities.boss.Analyzer;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class GainListPacketBuilder {

    private static final int MAX_PACKET_SIZE = 256;

    private static Analyzer.LEVEL getLevel(int number) {
        for (Analyzer.LEVEL level: Analyzer.LEVEL.values()) {
            if (level.getNumber() == number) {
                return level;
            }
        }
        return null;
    }

    public static byte[] encode(GainListPacket packet) {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
        Analyzer.Gain[] gains = packet.getGains();
        buffer.putInt(gains.length);
        for (Analyzer.Gain gain: gains) {
            buffer.putInt(gain.getLevel().getNumber());
            buffer.putFloat(gain.getValue());
        }
        return buffer.array();
    }

    public static GainListPacket decode(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int count = buffer.getInt();
        ArrayList<Analyzer.Gain> gains = new ArrayList<Analyzer.Gain>(count);
        for (int i = 0; i < count; i += 1) {
            Analyzer.LEVEL level = getLevel(buffer.getInt());
            float value = buffer.getFloat();
            gains.add(new Analyzer.Gain(level, value));
        }
        Analyzer.Gain[] content = new Analyzer.Gain[count];
        return new GainListPacket(gains.toArray(content));
    }
}
