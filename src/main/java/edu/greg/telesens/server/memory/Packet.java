package edu.greg.telesens.server.memory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Phoenix on 11.12.2016.
 */
public class Packet {
    protected AtomicBoolean inPartition = new AtomicBoolean(false);
    private PacketPartition partition;
    private String sessionId;
    private ByteFrame audionFrame;

    public Packet(PacketPartition packetPartition) {
        this.partition = packetPartition;
    }

    void reset() {
        sessionId = null;
    }

    public void recycle() {
        partition.recycle(this);
    }

    @Override
    public Packet clone() {
        Packet packet = PacketMemory.allocate();
        return packet;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setAudionFrame(ByteFrame audionFrame) {
        this.audionFrame = audionFrame;
    }

    public ByteFrame getAudionFrame() {
        return audionFrame;
    }
}
