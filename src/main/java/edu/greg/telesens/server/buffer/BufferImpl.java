package edu.greg.telesens.server.buffer;

import edu.greg.telesens.server.memory.Packet;
import lombok.extern.slf4j.Slf4j;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by SKulik on 12.12.2016.
 */
@Slf4j
public class BufferImpl implements Buffer {

    private Deque<Packet> queue = new ConcurrentLinkedDeque<>();
    private BufferEventHandler handler;
    private AtomicBoolean handled = new AtomicBoolean(false);
    private int bufferSize;
    private int minBufferSize;

    public BufferImpl(int bufferSize, int minBufferSize) {
        this.bufferSize = bufferSize;
        this.minBufferSize = minBufferSize;
    }

    @Override
    public Packet get(String sessionId) {
        if (queue.size() <= minBufferSize && !handled.get()) {
//            log.debug("queue size {}", queue.size());
            notify(sessionId);
        }
        return queue.poll();
    }

    @Override
    public void rollback(Packet packet) {
        queue.offerFirst(packet);
    }

    @Override
    public void notify(String sessionId) {
        if (handler != null && !handled.get()) {
            handled.set(true);
            handler.handle(sessionId, bufferSize);
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
