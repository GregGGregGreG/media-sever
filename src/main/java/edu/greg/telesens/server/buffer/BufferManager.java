package edu.greg.telesens.server.buffer;

/**
 * Created by Phoenix on 11.12.2016.
 */
public interface BufferManager {
    void start();
    void stop();
    Buffer createBuffer();
}
