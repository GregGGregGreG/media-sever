package edu.greg.telesens.server.buffer;

/**
 * Created by Phoenix on 11.12.2016.
 */
public interface Buffer {
//    get new packet
    Packet get(String sessionId);
//    notify when no packets
    void notify(String sessionId);
//    set event handler
    void setEventHandler(BufferEventHandler handler);
//    cleanup
    void cleanup();
}
