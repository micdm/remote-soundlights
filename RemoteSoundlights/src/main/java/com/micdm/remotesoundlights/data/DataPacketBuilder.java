package com.micdm.remotesoundlights.data;

import java.nio.ByteBuffer;

public class DataPacketBuilder {

    private static final long MARK = 4611586017427387804L;

    public static byte[] encode(DataPacket packet) {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.putLong(MARK);
        buffer.putLong(packet.getSender());
        buffer.putLong(packet.getContent());
        return buffer.array();
    }

    public static DataPacket decode(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        long mark = buffer.getLong();
        if (mark != MARK) {
            return null;
        }
        long sender = buffer.getLong();
        long content = buffer.getLong();
        return new DataPacket(sender, content);
    }
}
