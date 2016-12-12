package edu.greg.telesens.server.channel;

import edu.greg.telesens.server.NamedThreadFactory;
import edu.greg.telesens.server.resource.ResourceWorker;
import edu.greg.telesens.server.session.ClientSession;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by SKulik on 12.12.2016.
 */
@Component
public class ChannelManagerImpl implements ChannelManager, InitializingBean, DisposableBean {

    private ScheduledExecutorService executorService;
    private List<Slot> workers = new CopyOnWriteArrayList<>();

    @Value("${channel.poolSize:1}")
    private int poolSize;

    @Value("${channel.sendInterval:10}")
    private long sendInterval;

    @Override
    public void start() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
        workers.clear();
        executorService = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory("resource"));

        for (int i = 0; i < poolSize; i++) {
            Slot slot = new Slot();
            slot.setCount(0);
            slot.setWorker(new ChannelWorkerImpl(this));
            workers.add(slot);
            executorService.scheduleAtFixedRate(slot.getWorker(), sendInterval, sendInterval, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void stop() {
        executorService.shutdownNow();
        executorService = null;
        workers.clear();
    }

    @Override
    public ChannelWorker getFreeWorker() {
        Slot slot = null;
        int count = Integer.MAX_VALUE;
        for (Slot s: workers) {
            if (s.getCount() < count) {
                count = s.getCount();
                slot = s;
            }
        }
        return slot.getWorker();
    }

    @Override
    public void useWorker(ChannelWorker worker, ClientSession session) {
        Slot slot = null;
        for (Slot s: workers) {
            if (s.getWorker() == worker) {
                slot = s;
            }
        }
        slot.incCount();
    }

    @Override
    public void freeWorker(ChannelWorker worker, ClientSession session) {
        Slot slot = null;
        for (Slot s: workers) {
            if (s.getWorker() == worker) {
                slot = s;
            }
        }
        slot.decCount();
    }

    @Override
    public void destroy() throws Exception {
        stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    private static class Slot {
        private ChannelWorker worker;
        private volatile int count;
        private Lock lock = new ReentrantLock();

        public ChannelWorker getWorker() {
            return worker;
        }

        public void setWorker(ChannelWorker worker) {
            this.worker = worker;
        }

        public int getCount() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }

        public void setCount(int count) {
            lock.lock();
            try {
                this.count = count;
            } finally {
                lock.unlock();
            }
        }

        public void incCount() {
            lock.lock();
            try {
                this.count++;
            } finally {
                lock.unlock();
            }
        }

        public void decCount() {
            lock.lock();
            try {
                this.count--;
            } finally {
                lock.unlock();
            }
        }
    }
}
