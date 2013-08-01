package com.micdm.remotesoundlights.data;

public class DataPacket {

    private long sender;
    private long content;

    public DataPacket(long sender, long content) {
        this.sender = sender;
        this.content = content;
    }

    public long getSender() {
        return sender;
    }

    public long getContent() {
        return content;
    }
}
