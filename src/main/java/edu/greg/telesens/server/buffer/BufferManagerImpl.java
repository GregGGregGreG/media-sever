package edu.greg.telesens.server.buffer;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by SKulik on 12.12.2016.
 */
@Component
public class BufferManagerImpl implements BufferManager, InitializingBean, DisposableBean {

    private List<Buffer> buffers = new CopyOnWriteArrayList<>();

    @Value("${buffers.genBufferSize:10}")
    private int bufferSize;

    @Value("${buffers.minBufferSize:20}")
    private int minBufferSize;


    @Override
    public void start() {
        stop();
    }

    @Override
    public void stop() {
        if (!buffers.isEmpty()) {
            for (Buffer buf : buffers) {
                buf.cleanup();
            }
            buffers.clear();
        }
    }

    @Override
    public Buffer createBuffer() {
        Buffer buffer = new BufferImpl(bufferSize, minBufferSize);
        buffers.add(buffer);
        return buffer;
    }

    @Override
    public void destroy() throws Exception {
        stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }
}
