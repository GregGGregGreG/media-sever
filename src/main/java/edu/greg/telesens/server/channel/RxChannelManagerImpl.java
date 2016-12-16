package edu.greg.telesens.server.channel;

import edu.greg.telesens.server.NamedThreadFactory;
import edu.greg.telesens.server.network.rtp.AVProfile;
import edu.greg.telesens.server.network.rtp.RTPFormats;
import edu.greg.telesens.server.session.ClientSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by SKulik on 13.12.2016.
 */
@Slf4j
@Component
public class RxChannelManagerImpl implements RxChannelManager, InitializingBean, DisposableBean {

    private ScheduledExecutorService executorService;
    private List<RxChannelWorker> workers = new CopyOnWriteArrayList<>();

    @Value("${channel.rxPoolSize:4}")
    private int poolSize;

//    RxChannelManager

    @Override
    public void start() throws IOException {
        log.debug("Start RxChannelManagerImpl with {} worker ", poolSize);
        executorService = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory("rxChannel"));
        for (int i = 0; i < poolSize; i++) {
            RxChannelWorker worker = new RxChannelWorker(this);
            workers.add(worker);
            executorService.scheduleAtFixedRate(worker, 10, 10, TimeUnit.MILLISECONDS);
        }
        log.debug("Started RxChannelManagerImpl with {} worker ", workers.size());
    }

    @Override
    public void stop() throws IOException {
        log.debug("Stop RxChannelManagerImpl with {} worker ", poolSize);
        for (RxChannelWorker worker : workers) {
            worker.stop();
        }
        executorService.shutdownNow();
        executorService = null;
        workers.clear();
    }

    @Override
    public void register(ClientSession session, ClientChannel channel) throws IOException {
        log.debug("Register new task for dtmf detector: Id session {} --> Dest Address {}", session.getSessionId(), channel.getDstSockAddr().toString());
        getWorkerWithMinimalLoading().register(session, channel);
    }

    @Override
    public void unregister(String sessionId) throws IOException {
        log.debug("Unregister  task for dtmf detector: Id session {} ", sessionId);
        for (RxChannelWorker task : workers) {
            task.unregister(sessionId);
        }
    }

    @Override
    public RTPFormats getRtpFormats() {
        return AVProfile.audio;
    }


//    DisposableBean


    @Override
    public void destroy() throws Exception {
        stop();
    }

//    InitializingBean

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }


//    HELPER

    private RxChannelWorker getWorkerWithMinimalLoading() {
        RxChannelWorker RxChannelWorker = null;
        int loading = Integer.MAX_VALUE;
        for (RxChannelWorker task : workers) {
            if (task.getLoading().get() < loading) {
                RxChannelWorker = task;
                loading = task.getLoading().get();
            }
        }
        return RxChannelWorker;
    }
}
