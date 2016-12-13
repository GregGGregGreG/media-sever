package edu.greg.telesens.server.channel;

import edu.greg.telesens.server.NamedThreadFactory;
import edu.greg.telesens.server.network.rtp.AVProfile;
import edu.greg.telesens.server.network.rtp.RTPFormats;
import edu.greg.telesens.server.session.ClientSession;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by SKulik on 13.12.2016.
 */
@Component
public class RxChannelManagerImpl implements RxChannelManager, InitializingBean, DisposableBean {

    private ScheduledExecutorService executorService;
    private List<RxChannelWorker> tasks = new CopyOnWriteArrayList<>();

    @Value("${channel.rxPoolSize:4}")
    private int poolSize;

    @Override
    public void start() throws IOException {
        executorService = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory("resource"));
        for (int i = 0; i < poolSize; i++) {
            RxChannelWorker worker = new RxChannelWorker(this);
            tasks.add(worker);
            executorService.submit(worker);
        }
    }

    @Override
    public void stop() throws IOException {
        for (RxChannelWorker worker: tasks) {
            worker.stop();
        }
        executorService.shutdownNow();
        executorService = null;
        tasks.clear();
    }

    @Override
    public void register(ClientSession session, ClientChannel channel) throws IOException {
        getTaskWithMinimalLoading().register(session, channel);
    }

    private RxChannelWorker getTaskWithMinimalLoading() {
        RxChannelWorker RxChannelWorker = null;
        int loading = Integer.MAX_VALUE;
        for (RxChannelWorker task: tasks) {
            if (task.getLoading().get() < loading) {
                RxChannelWorker = task;
                loading = task.getLoading().get();
            }
        }
        return RxChannelWorker;
    }

    @Override
    public void unregister(String sessionId) throws IOException {
        for (RxChannelWorker task: tasks) {
            task.unregister(sessionId);
        }
    }

    @Override
    public RTPFormats getRtpFormats() {
        return AVProfile.audio;
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
