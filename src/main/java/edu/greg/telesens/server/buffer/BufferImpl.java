package edu.greg.telesens.server.buffer;

import edu.greg.telesens.server.memory.Packet;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by SKulik on 12.12.2016.
 */
public class BufferImpl implements Buffer {

    private Queue<Packet> queue = new ConcurrentLinkedQueue<>();
    private BufferEventHandler handler;
    private AtomicBoolean handled = new AtomicBoolean(false);
    private int bufferSize;

    public BufferImpl(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public Packet get(String sessionId) {
        return queue.poll();
    }

    @Override
    public void notify(String sessionId) {
        if (handler != null && !handled.get()) {
            handler.handle(sessionId, bufferSize);
            handled.set(true);
        }
    }

    @Override
    public void setEventHandler(BufferEventHandler handler) {
        this.handler = handler;
    }

    @Override
    public void cleanup() {
        if (handler != null) {
            handler.bufferStopped();
            handler = null;
        }
        queue.clear();
    }

    @Override
    public void put(Packet packet) {
        queue.offer(packet);
    }

    @Override
    public void resourceComplete(String sessionId) {
        handled.set(false);
    }
}
