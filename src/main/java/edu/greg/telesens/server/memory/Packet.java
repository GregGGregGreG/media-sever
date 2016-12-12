package edu.greg.telesens.server.memory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Phoenix on 11.12.2016.
 */
public class Packet {
    protected AtomicBoolean inPartition = new AtomicBoolean(false);
    private PacketPartition partition;
    private String sessionId;
    private ByteFrame audioFrame;
    private long timestamp;
    private long realTime;

    public Packet(PacketPartition packetPartition) {
        this.partition = packetPartition;
    }

    void reset() {
        sessionId = null;
        audioFrame.recycle();
        audioFrame = null;
        timestamp = 0;
        realTime = 0;
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

    public void setAudioFrame(ByteFrame audionFrame) {
        this.audioFrame = audionFrame;
    }

    public ByteFrame getAudioFrame() {
        return audioFrame;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getRealTime() {
        return realTime;
    }

    public void setRealTime(long realTime) {
        this.realTime = realTime;
    }
}
