package edu.greg.telesens.server.resource;

import edu.greg.telesens.server.NamedThreadFactory;
import edu.greg.telesens.server.session.ClientSession;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Phoenix on 11.12.2016.
 */
@Component
public class ResourceManagerImpl implements ResourceManager, DisposableBean, InitializingBean {

    private List<ResourceWorker> workers = new CopyOnWriteArrayList<>();
    private ScheduledExecutorService executorService;

    @Value("${resources.codecPoolSize:1}")
    private int poolSize;

    @Override
    public ResourceWorker createWorker(ClientSession session) {
        return null;
    }

    @Override
    public void start() {
        executorService = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory("resource"));
    }

    @Override
    public void stop() {
        executorService.shutdownNow();
    }

    @Override
    public void submit(ResourceWorker worker) {
        executorService.submit(worker);
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
