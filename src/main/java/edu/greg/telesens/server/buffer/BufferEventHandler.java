package edu.greg.telesens.server.buffer;

/**
 * Created by Phoenix on 11.12.2016.
 */
public interface BufferEventHandler {
//    handler event
    void handle(String sessionId, int packetCount);
}
